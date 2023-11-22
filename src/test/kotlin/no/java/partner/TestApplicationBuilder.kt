package no.java.partner

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.typesafe.config.ConfigFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.java.partner.plugins.UserInfo
import no.java.partner.plugins.buildToken
import no.java.partner.plugins.configureOpenApi
import no.java.partner.plugins.configureRouting
import no.java.partner.plugins.configureSecurity
import no.java.partner.plugins.configureSerialization

val appConfig = HoconApplicationConfig(
    ConfigFactory.parseMap(
        mapOf(
            "jwt" to mapOf(
                "secret" to "secret",
                "issuer" to "issuer",
                "audience" to "audience",
                "realm" to "Utegang",
            ),
            "github" to mapOf(
                "client_id" to "client_id",
                "client_secret" to "client_secret",
                "host_and_port" to "host_and_port",
                "admins" to listOf("admin"),
            ),
        ),
    ),
)

fun ApplicationTestBuilder.testClient() = createClient {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
}

fun ApplicationTestBuilder.env() {
    environment {
        config = appConfig
    }
}

fun ApplicationTestBuilder.app(config: Application.() -> Unit) {
    application {
        configureSerialization()
        configureRouting()
        configureSecurity(mockk())
        configureOpenApi("Test", 8080)

        config()
    }
}

fun buildTestToken() = buildToken(appConfig, UserInfo("username", "FullName", "Login", "Email"))
