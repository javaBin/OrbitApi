package no.java.partner.plugins

import io.ktor.server.application.Application
import kotliquery.sessionOf
import no.java.partner.repository.ListRepository
import no.java.partner.repository.PartnerRepository
import no.java.partner.service.ListService
import no.java.partner.service.PartnerService
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun Application.configureServices(dataSource: DataSource) {
    Flyway.configure().dataSource(dataSource).load().migrate()

    val dbSession = sessionOf(dataSource, returnGeneratedKey = true)

    val partnerRepository = PartnerRepository(dbSession)
    val listRepository = ListRepository(dbSession)

    val partnerService = PartnerService(partnerRepository)
    val listService = ListService(listRepository)

    configurePartnerRouting(partnerService)
    configureListRouting(listService)
}
