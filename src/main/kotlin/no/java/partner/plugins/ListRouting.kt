package no.java.partner.plugins

import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.partner.model.web.toBasicInfoList
import no.java.partner.model.web.toInfoListWithContacts
import no.java.partner.plugins.openapi.ListRoutingDoc
import no.java.partner.service.ListService

fun Application.configureListRouting(service: ListService) {
    val optionalSec = (environment.config.property("jwt.security_on").getString() != "true")
    routing {
        authenticate("auth-jwt", optional = optionalSec) {
            route("/list") {
                install(NotarizedRoute()) {
                    get = ListRoutingDoc.listList
                    post = ListRoutingDoc.createList
                }

                get {
                    call.respond(HttpStatusCode.OK, service.allLists().map { it.toBasicInfoList() })
                }

                post {
                    service.createList(call.receive()).map { it.toBasicInfoList() }.respond()
                }

                route("/{id}") {
                    install(NotarizedRoute()) {
                        get = ListRoutingDoc.listById
                    }

                    get {
                        service.listById(call.parameters["id"]?.toLong()).map { it.toInfoListWithContacts() }.respond()
                    }

                    route("/contact") {
                        contactRoute(service)
                    }
                }
            }
        }
    }
}

private fun Route.contactRoute(service: ListService) {
    route("/{contact}") {
        install(NotarizedRoute()) {
            post = ListRoutingDoc.createSubscription
        }

        post {
            service.createSubscription(
                listId = call.parameters["id"]?.toLong(),
                contactId = call.parameters["contact"]?.toLong(),
            ).map { it.toInfoListWithContacts() }.respond()
        }

        route("/subscribe") {
            install(NotarizedRoute()) {
                patch = ListRoutingDoc.subscribe
            }

            patch {
                service.updateSubscription(
                    listId = call.parameters["id"]?.toLong(),
                    contactId = call.parameters["contact"]?.toLong(),
                    subscription = true,
                ).map { it.toInfoListWithContacts() }.respond()
            }
        }

        route("/unsubscribe") {
            install(NotarizedRoute()) {
                patch = ListRoutingDoc.unsubscribe
            }

            patch {
                service.updateSubscription(
                    listId = call.parameters["id"]?.toLong(),
                    contactId = call.parameters["contact"]?.toLong(),
                    subscription = false,
                ).map { it.toInfoListWithContacts() }.respond()
            }
        }
    }
}
