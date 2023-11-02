package no.java.partner.repository

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import no.java.partner.model.Partner
import org.intellij.lang.annotations.Language

class PartnerRepository(private val session: Session) {
    companion object {
        @Language("PostgreSQL")
        val ALL_QUERY = """
        SELECT p.id AS p_id, p.domainName AS p_domainName, p.name AS p_name
        FROM partner p
        """.trimIndent()

        @Language("PostgreSQL")
        val BY_ID_QUERY = """
        SELECT p.id AS p_id, p.domainName AS p_domainName, p.name AS p_name
        FROM partner p
        WHERE p.id = :id  
        """.trimIndent()
    }

    fun all() = session.run(
        queryOf(ALL_QUERY).map
            { it.toPartner() }.asList,
    )

    fun byId(id: Long) = session.run(
        queryOf(
            statement = BY_ID_QUERY,
            paramMap = mapOf("id" to id),
        ).map { it.toPartner() }.asSingle,
    )
}

fun Row.toPartner() = Partner(
    id = this.long("p_id"),
    name = this.string("p_name"),
    domainName = this.stringOrNull("p_domainName"),
    contacts = emptyList(),
)
