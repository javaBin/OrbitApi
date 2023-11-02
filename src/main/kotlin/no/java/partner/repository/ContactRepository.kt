package no.java.partner.repository

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import no.java.partner.model.Contact
import org.intellij.lang.annotations.Language

class ContactRepository(private val session: Session) {
    companion object {
        @Language("PostgreSQL")
        val ALL_QUERY = """
        SELECT
          c.id AS c_id, c.name AS c_name, c.email AS c_email, c.telephone as c_tel
        FROM contact c
        """.trimIndent()

        @Language("PostgreSQL")
        val ALL_FULL_QUERY = """
        SELECT
          c.id AS c_id, c.name AS c_name, c.email AS c_email, c.telephone as c_tel,
          p.id AS p_id, p.domainName AS p_domainName, p.name AS p_name
        FROM contact c, partner p WHERE c.partner_id = p.id
        """.trimIndent()
    }

    fun all() = session.run(
        queryOf(ALL_QUERY).map
            { it.toContact() }.asList,
    )

    fun allFull() = session.run(
        queryOf(ALL_FULL_QUERY).map
            { it.toContact() }.asList,
    )
}

fun Row.toContact() = Contact(
    id = this.long("c_id"),
    name = this.stringOrNull("c_name"),
    email = this.string("c_email"),
    telephone = this.stringOrNull("c_telephone"),
    partner = this.longOrNull("p_id")?.let { this.toPartner() },
    lists = emptyList(),
)
