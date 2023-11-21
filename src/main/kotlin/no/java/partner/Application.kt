package no.java.partner

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import no.java.partner.plugins.configureMonitoring
import no.java.partner.plugins.configureRouting
import no.java.partner.plugins.configureSecurity
import no.java.partner.plugins.configureSerialization
import no.java.partner.plugins.configureServices
import no.java.partner.plugins.dataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.httpClient() = HttpClient(OkHttp) {
    install(Logging)

    install(ContentNegotiation) {
        jackson()
    }
}

fun Application.module() {
    val dataSource = dataSource()
    val httpClient = httpClient()

    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureSecurity(
        httpClient,
    )
    configureServices(dataSource)
}
