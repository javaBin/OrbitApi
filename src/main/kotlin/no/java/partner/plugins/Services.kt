package no.java.partner.plugins

import io.ktor.server.application.Application
import kotliquery.sessionOf
import mu.KotlinLogging
import no.java.partner.repository.PartnerRepository
import org.flywaydb.core.Flyway
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

fun Application.configureServices(dataSource: DataSource) {
    Flyway.configure().dataSource(dataSource).load().migrate()

    val dbSession = sessionOf(dataSource, returnGeneratedKey = true)

    logger.info { "Database connected using ${dbSession.connection.driverName}" }

    val partnerRepository = PartnerRepository(dbSession)

    logger.info { "Partner count: ${partnerRepository.all().count()}" }

    // Create repositories
    // TODO
    // Create services
    // TODO
}
