package no.java.partner.plugins

import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.server.Server
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.typeOf

fun Application.configureOpenApi(version: String, port: Int) {
    install(NotarizedApplication()) {
        spec = OpenApiSpec(
            jsonSchemaDialect = "https://spec.openapis.org/oas/3.1/dialect/base",
            info = Info(
                "Orbit API",
                version = version,
                description = "API for managing javaBin partners",
            ),
            servers = mutableListOf(
                Server(
                    url = URI("http://localhost:$port"),
                    description = "Dev",
                ),
            ),
            components = Components(
                securitySchemes = mutableMapOf(
                    "auth-jwt" to BearerAuth(),
                ),
            ),
        )
        customTypes =
            mapOf(
                typeOf<Instant>() to TypeDefinition(type = "string", format = "date-time"),
                typeOf<LocalDate>() to TypeDefinition(type = "string", format = "date"),
                typeOf<LocalDateTime>() to TypeDefinition(type = "string", format = "date-time"),
            )
    }

    routing {
        swagger(pageTitle = "javaBin Partner API")
    }
}
