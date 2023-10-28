package no.java.partner

import io.ktor.server.application.Application
import no.java.partner.plugins.configureMonitoring
import no.java.partner.plugins.configureRouting
import no.java.partner.plugins.configureSerialization
import no.java.partner.plugins.configureServices
import no.java.partner.plugins.dataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val dataSource = dataSource()

    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureServices(dataSource)
}
