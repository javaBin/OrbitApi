package no.java.partner.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.partner.model.web.toBasicInfoList
import no.java.partner.model.web.toInfoListWithContacts
import no.java.partner.service.ListService

fun Application.configureListRouting(service: ListService) {
    routing {
        authenticate("auth-jwt") {
            route("/list") {
                get {
                    call.respond(HttpStatusCode.OK, service.allLists().map { it.toBasicInfoList() })
                }

                post {
                    service.createList(call.receive()).map { it.toBasicInfoList() }.respond()
                }

                route("/{id}") {
                    get {
                        service.listById(call.parameters["id"]?.toLong()).map { it.toInfoListWithContacts() }.respond()
                    }

                    route("/contact") {
                        route("/{contact}") {
                            post {
                                service.createSubscription(
                                    listId = call.parameters["id"]?.toLong(),
                                    contactId = call.parameters["contact"]?.toLong(),
                                ).map { it.toInfoListWithContacts() }.respond()
                            }

                            patch("/subscribe") {
                                service.updateSubscription(
                                    listId = call.parameters["id"]?.toLong(),
                                    contactId = call.parameters["contact"]?.toLong(),
                                    subscription = true,
                                ).map { it.toInfoListWithContacts() }.respond()
                            }

                            patch("/unsubscribe") {
                                service.updateSubscription(
                                    listId = call.parameters["id"]?.toLong(),
                                    contactId = call.parameters["contact"]?.toLong(),
                                    subscription = false,
                                ).map { it.toInfoListWithContacts() }.respond()
                            }
                        }
                    }
                }
            }
        }
    }
}
