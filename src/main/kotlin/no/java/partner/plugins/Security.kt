package no.java.partner.plugins

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.date.GMTDate
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.partner.ApiError
import no.java.partner.GithubCallFailed
import no.java.partner.MissingPrincipal
import no.java.partner.NonAdminLogin
import java.time.ZonedDateTime
import java.util.Date
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

private const val COOKIE_LIFETIME = 10L

fun HttpRequestBuilder.githubHeaders(token: String) = this.headers {
    append("Content-Type", "application/json")
    append("X-Requested-With", "XMLHttpRequest")
    append("X-GitHub-Api-Version", "2022-11-28")
    append("Accept", "application/vnd.github.v3+json")
    append("Authorization", "Bearer $token")
}

fun buildToken(config: ApplicationConfig, userInfo: UserInfo): String = JWT.create()
    .withAudience(config.property("jwt.audience").getString())
    .withIssuer(config.property("jwt.issuer").getString())
    .withClaim("github_id", userInfo.id)
    .withClaim("github_name", userInfo.name)
    .withClaim("github_login", userInfo.login)
    .withClaim("github_email", userInfo.email)
    .withExpiresAt(Date(System.currentTimeMillis() + (8.hours.inWholeMilliseconds)))
    .sign(Algorithm.HMAC256(config.property("jwt.secret").getString()))

fun Application.configureSecurity(
    httpClient: HttpClient,
) {
    fun buildToken(userInfo: UserInfo): String = buildToken(environment.config, userInfo)

    fun jwtVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(environment.config.property("jwt.secret").getString()))
        .withAudience(environment.config.property("jwt.audience").getString())
        .withIssuer(environment.config.property("jwt.issuer").getString())
        .build()

    suspend fun userInfo(token: String): Either<ApiError, UserInfo> {
        logger.debug { "Getting user info with token $token" }
        val response = httpClient.get("https://api.github.com/user") {
            githubHeaders(token)
        }

        if (!response.status.isSuccess()) {
            return GithubCallFailed(response.status, response.body()).left()
        }

        return response.body<UserInfo>().right()
    }

    fun validateAdmin(userInfo: UserInfo): Either<ApiError, UserInfo> {
        return when {
            environment.config.property("github.admins").getList()
                .none { it.equals(userInfo.login, ignoreCase = true) } -> {
                NonAdminLogin.left()
            }

            else -> userInfo.right()
        }
    }

    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    val clientId = environment.config.property("github.client_id").getString()
    val clientSecret = environment.config.property("github.client_secret").getString()

    val hostAndPort = environment.config.property("github.host_and_port").getString()

    install(Authentication) {
        oauth("oauth-github") {
            urlProvider = { "$hostAndPort/login/oauth2/code/github" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "github",
                    authorizeUrl = "https://github.com/login/oauth/authorize",
                    accessTokenUrl = "https://github.com/login/oauth/access_token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = listOf("read:user", "admin:org"),
                )
            }
            client = httpClient
        }

        jwt("auth-jwt") {
            realm = jwtRealm

            verifier(jwtVerifier())

            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    routing {
        authenticate("oauth-github") {
            get("/login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/login/oauth2/code/github") {
                either {
                    val principal = call.oauthPrincipal().bind()

                    val userInfo = userInfo(principal.accessToken).bind()

                    val adminUserInfo = validateAdmin(userInfo).bind()

                    val jwt = buildToken(adminUserInfo)

                    call.response.cookies.append(
                        name = "user_session",
                        value = jwt,
                        path = "/",
                        expires = ZonedDateTime.now().cookieExpiry(COOKIE_LIFETIME),
                    )
                }.respondRedirect("/")
            }
        }
    }
}

fun ApplicationCall.oauthPrincipal() = when (val principal = this.principal<OAuthAccessTokenResponse.OAuth2>()) {
    null -> MissingPrincipal.left()
    else -> principal.right()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserInfo(
    val login: String,
    val id: String,
    val name: String,
    val email: String,
)

private fun ZonedDateTime.cookieExpiry(seconds: Long) =
    GMTDate(this.plusSeconds(seconds).toEpochSecond().seconds.inWholeMilliseconds)
