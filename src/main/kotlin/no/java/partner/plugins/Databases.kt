package no.java.partner.plugins

import io.ktor.server.application.Application
import kotliquery.HikariCP
import javax.sql.DataSource

fun Application.dataSource(): DataSource {
    return HikariCP.default(
        environment.config.property("postgres.url").getString(),
        environment.config.property("postgres.user").getString(),
        environment.config.property("postgres.password").getString(),
    )
}
