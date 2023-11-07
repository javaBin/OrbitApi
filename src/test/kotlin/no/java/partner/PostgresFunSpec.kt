package no.java.partner

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer

abstract class PostgresFunSpec(body: FunSpec.(Session) -> Unit) : FunSpec({
    val ds = ds()

    val cleanupCall = ds.connection.prepareCall("CALL cleanup()")

    beforeEach {
        cleanupCall.executeUpdate()
    }

    body(sessionOf(ds, returnGeneratedKey = true))
})

fun Session.loadFixtures(queries: List<String>) {
    queries.forEach { query ->
        this.run(queryOf(query).asExecute)
    }
}

private fun ds(): HikariDataSource {
    val db = db()
    return HikariConfig().apply {
        jdbcUrl = db.jdbcUrl
        username = db.username
        password = db.password
        maximumPoolSize = 5
        minimumIdle = 1
        idleTimeout = 500001
        connectionTimeout = 10000
        maxLifetime = 600001
        initializationFailTimeout = 5000
    }
        .let(::HikariDataSource)
        .also {
            it.flyway()
            it.installCleanup()
        }
}

private fun db() = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
    withReuse(true)
    start()
    println("🎩 Postgres started on port $firstMappedPort")
}

private fun HikariDataSource.flyway() {
    Flyway.configure()
        .dataSource(this)
        .cleanDisabled(false)
        .load()
        .also { it.clean() }
        .migrate()
}

private fun HikariDataSource.installCleanup() {
    val query = """
        CREATE OR REPLACE PROCEDURE cleanup()
        LANGUAGE plpgsql
        AS $$
        BEGIN
            DELETE FROM contact_list;
            DELETE FROM list;
            DELETE FROM contact;
            DELETE FROM partner;
        END;
        $$
    """.trimMargin()

    sessionOf(this).run(queryOf(query).asExecute)
}
