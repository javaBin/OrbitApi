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
        SELECT c.id, c.name, c.email, c.telephone
        FROM contact c
        """.trimIndent()

        @Language("PostgreSQL")
        val ALL_FULL_QUERY = """
        SELECT c.id, c.name, c.email, c.telephone, p.id, p.domainName, p.name
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
    id = this.long("id"),
    name = this.stringOrNull("name"),
    email = this.string("email"),
    telephone = this.stringOrNull("telephone"),
    partner = this.longOrNull("p.id")?.let { this.toPartner() },
    lists = emptyList(),
)
