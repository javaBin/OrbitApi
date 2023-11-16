package no.java.partner.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.java.partner.service.ListService

fun Application.configureListRouting(service: ListService) {
    routing {
        route("/list") {
            get {
                call.respond(HttpStatusCode.OK, service.allLists())
            }

            route("/{id}") {
                get {
                    call.apiRespond {
                        service.listById(call.parameters["id"]?.toLong())
                    }
                }
            }
        }
    }
}