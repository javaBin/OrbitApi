package no.java.partner.plugins

import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import no.java.partner.model.web.CreateContact
import no.java.partner.model.web.CreatePartner
import no.java.partner.model.web.toBasicPartner
import no.java.partner.model.web.toNewPartner
import no.java.partner.model.web.toPartnerWithContacts
import no.java.partner.plugins.openapi.PartnerRoutingDoc
import no.java.partner.service.PartnerService



fun Application.configurePartnerRouting(service: PartnerService) {
    val optionalSec = (environment.config.property("jwt.security_on").getString() != "false")
    routing {
        authenticate("auth-jwt", optional = optionalSec) {
            route("/partner") {
                install(NotarizedRoute()) {
                    get = PartnerRoutingDoc.partnerList
                    post = PartnerRoutingDoc.createPartner
                }

                get {
                    call.respond(HttpStatusCode.OK, service.allPartners().map { it.toBasicPartner() })
                }

                post {
                    service.createPartner(call.receive<CreatePartner>().toNewPartner()).map { it.toBasicPartner() }
                        .respond()
                }

                route("/{id}") {
                    install(NotarizedRoute()) {
                        get = PartnerRoutingDoc.partnerById
                        post = PartnerRoutingDoc.createContact
                    }

                    get {
                        service.partnerById(call.parameters["id"]?.toLong()).map { it.toPartnerWithContacts() }
                            .respond()
                    }

                    post("/contact") {
                        service.createContact(call.parameters["id"]?.toLong(), call.receive<CreateContact>())
                            .map { it.toPartnerWithContacts() }.respond()
                    }
                }
            }
        }
    }
}
