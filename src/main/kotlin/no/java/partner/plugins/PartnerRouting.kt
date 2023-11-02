package no.java.partner.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.partner.service.PartnerService

fun Application.configurePartnerRouting(service: PartnerService) {
    routing {
        route("/partner") {
            get("/") {
                call.respond(HttpStatusCode.OK, service.allPartners())
            }

            get("/{id}") {
                call.apiRespond {
                    service.partnerById(call.parameters["id"])
                }
            }
        }
    }
}
