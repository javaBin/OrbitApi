package no.java.partner

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import no.java.partner.plugins.configureMonitoring
import no.java.partner.plugins.configureOpenApi
import no.java.partner.plugins.configureRouting
import no.java.partner.plugins.configureSecurity
import no.java.partner.plugins.configureSerialization
import no.java.partner.plugins.configureServices
import no.java.partner.plugins.dataSource
import java.util.Properties

fun main(args: Array<String>){

    io.ktor.server.netty.EngineMain.main(args)
}

object VersionConfig {
    private val versionProps by lazy {
        Properties().also {
            it.load(this.javaClass.getResourceAsStream("/version.properties"))
        }
    }

    val version by lazy {
        versionProps.getProperty("version") ?: "no version"
    }
}

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
    configureOpenApi(
        version = VersionConfig.version,
        port = environment.config.property("ktor.deployment.port").getString().toInt(),
    )
    //if (environment.config.property("jwt.security_on").getString() != "false") {
        configureSecurity(
            httpClient,
        )
    //}
    configureServices(dataSource)
}
