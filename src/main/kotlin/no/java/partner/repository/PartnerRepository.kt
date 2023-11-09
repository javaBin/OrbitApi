package no.java.partner.repository

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import no.java.partner.model.Contact
import no.java.partner.model.Partner
import no.java.partner.model.web.CreateContact
import no.java.partner.model.web.CreatePartner
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
        FROM partner p LEFT OUTER JOIN contact c ON c.partner_id = p.id
        WHERE p.id = :id
        """.trimIndent()

        @Language("PostgreSQL")
        val CREATE_PARTNER = """
        INSERT INTO partner (name, domainName)
        VALUES(:name, :domainName)
        """.trimIndent()

        @Language("PostgreSQL")
        val CREATE_CONTACT = """
        INSERT INTO contact (name, email, telephone, source, partner_id)
        VALUES(:name, :email, :telephone, :source, :partnerId)
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

    fun createPartner(partner: CreatePartner) = session.run(
        queryOf(
            statement = CREATE_PARTNER,
            paramMap = mapOf("name" to partner.name, "domainName" to partner.domainName),
        ).asUpdateAndReturnGeneratedKey,
    )

    fun createContact(partnerId: Long, contact: CreateContact) = session.run(
        queryOf(
            statement = CREATE_CONTACT,
            paramMap = mapOf(
                "name" to contact.name,
                "email" to contact.email,
                "telephone" to contact.telephone,
                "source" to contact.source,
                "partnerId" to partnerId,
            ),
        ).asUpdateAndReturnGeneratedKey,
    )
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
)

fun Row.toContact() = Contact(
    id = this.long("c_id"),
    name = this.stringOrNull("c_name"),
    email = this.string("c_email"),
    telephone = this.stringOrNull("c_tel"),
    source = this.stringOrNull("c_source"),
    lists = emptyList(),
)
