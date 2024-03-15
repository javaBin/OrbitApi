package no.java.partner.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.warn { "Bad Request - ${cause.message}" }
            call.respondText(text = "Bad Request", status = HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}
