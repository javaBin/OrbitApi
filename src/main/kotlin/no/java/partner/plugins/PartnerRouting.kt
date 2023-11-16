package no.java.partner.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.partner.model.web.CreateContact
import no.java.partner.service.PartnerService

fun Application.configurePartnerRouting(service: PartnerService) {
    routing {
        route("/partner") {
            get {
                call.respond(HttpStatusCode.OK, service.allPartners())
            }

            post {
                call.apiRespond {
                    service.createPartner(call.receive())
                }
            }

            route("/{id}") {
                get {
                    call.apiRespond {
                        service.partnerById(call.parameters["id"]?.toLong())
                    }
                }

                post("/contact") {
                    call.apiRespond {
                        service.createContact(call.parameters["id"]?.toLong(), call.receive<CreateContact>())
                    }
                }
            }
        }
    }
}