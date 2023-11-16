package no.java.partner

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import no.java.partner.plugins.configureRouting
import no.java.partner.plugins.configureSerialization

fun ApplicationTestBuilder.testClient() = createClient {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
}

fun ApplicationTestBuilder.app(config: Application.() -> Unit) {
    application {
        configureSerialization()
        configureRouting()

        config()
    }
}
