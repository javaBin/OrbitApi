package no.java.partner.repository

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import no.java.partner.model.Contact
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
        SELECT
            p.id AS p_id, p.domainName AS p_domainName, p.name AS p_name,
            c.id AS c_id, c.name AS c_name, c.email AS c_email, c.telephone as c_tel, c.source as c_source
        FROM partner p, contact c
        WHERE p.id = :id AND c.partner_id = p.id
        """.trimIndent()
    }

    fun all() = session.run(
        queryOf(ALL_QUERY).map { it.toPartner() }.asList,
    )

    fun byId(id: Long) = session.run(
        queryOf(
            statement = BY_ID_QUERY,
            paramMap = mapOf("id" to id),
        ).map { it.toPartner(true) }.asList,
    ).mergeFold { p1, p2 ->
        p1.copy(contacts = p1.contacts + p2.contacts)
    }.firstOrNull()
}

fun Row.toPartner(withContacts: Boolean = false) = Partner(
    id = this.long("p_id"),
    name = this.string("p_name"),
    domainName = this.stringOrNull("p_domainName"),
    contacts = if (withContacts && this.longOrNull("c_id") != null) {
        listOf(this.toContact())
    } else {
        emptyList()
    },
).let { partner ->
    partner.copy(contacts = partner.contacts.map { contact -> contact.copy(partner = partner) })
}

fun Row.toContact() = Contact(
    id = this.long("c_id"),
    name = this.stringOrNull("c_name"),
    email = this.string("c_email"),
    telephone = this.stringOrNull("c_tel"),
    source = this.stringOrNull("c_source"),
    partner = null,
    lists = emptyList(),
)
