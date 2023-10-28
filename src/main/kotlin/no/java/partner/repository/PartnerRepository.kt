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
        SELECT *
        FROM partner
        """.trimIndent()
    }

    fun all() = session.run(
        queryOf(ALL_QUERY).map
            { it.toPartner() }.asList,
    )
}

fun Row.toPartner() = Partner(
    id = this.long("id"),
    name = this.string("name"),
    domainName = this.stringOrNull("domainName"),
    contacts = emptyList(),
)
