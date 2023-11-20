package no.java.partner.repository

import kotliquery.Session
import kotliquery.queryOf
import no.java.partner.model.NewPartner
import no.java.partner.model.web.CreateContact
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
            c.id AS c_id, c.name AS c_name, c.email AS c_email, c.telephone as c_tel, c.source as c_source,
            l.id AS l_id, l.name AS l_name,
            cl.subscribed AS cl_subscribed
        FROM partner p
            LEFT OUTER JOIN contact c ON c.partner_id = p.id
            LEFT OUTER JOIN contact_list cl ON cl.contact_id = c.id
            LEFT OUTER JOIN list l on cl.list_id = l.id
        WHERE p.id = :id AND (cl.subscribed IS true OR cl.subscribed IS NULL)
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
        ).map { it.toPartner(withContacts = true, withLists = true) }.asList,
    ).mergeFold { p1, p2 ->
        p1.copy(contacts = p1.contacts + p2.contacts)
    }.map { partner ->
        partner.copy(
            contacts = partner.contacts.mergeFold { c1, c2 ->
                c1.copy(lists = c1.lists + c2.lists)
            },
        )
    }.firstOrNull()

    fun createPartner(partner: NewPartner) = session.run(
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
