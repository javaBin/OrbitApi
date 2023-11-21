package no.java.partner.plugins

import arrow.core.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.util.pipeline.PipelineContext
import no.java.partner.ApiError
import no.java.partner.GithubCallFailed

context(PipelineContext<Unit, ApplicationCall>)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(status: HttpStatusCode = HttpStatusCode.OK) =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> call.respond(status, value)
    }

context(PipelineContext<Unit, ApplicationCall>)
suspend inline fun <reified A : Any> Either<ApiError, A>.respondRedirect(url: String) = when (this) {
    is Either.Left -> respond(value)
    is Either.Right -> call.respondRedirect(url)
}

suspend fun PipelineContext<Unit, ApplicationCall>.respond(error: ApiError) = when (error) {
    is GithubCallFailed -> call.respond(
        error.statusCode,
        mapOf(
            "Upstream Status Code" to error.upstreamStatusCode,
            "Upstream Body" to error.upstreamMessage,
        ),
    )

    else -> call.respond(error.statusCode, error.message)
}
