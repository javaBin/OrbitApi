package no.java.partner.repository

import kotliquery.Session
import kotliquery.queryOf
import org.intellij.lang.annotations.Language

class ListRepository(private val session: Session) {
    companion object {
        @Language("PostgreSQL")
        val ALL_QUERY = """
        SELECT l.id AS l_id, l.name AS l_name
        FROM list l
        """.trimIndent()

        @Language("PostgreSQL")
        val BY_ID_QUERY = """
        SELECT l.id AS l_id, l.name AS l_name,
            c.id AS c_id, c.name AS c_name, c.email AS c_email, c.telephone as c_tel, c.source as c_source,
            cl.subscribed AS cl_subscribed
        FROM list l
            LEFT OUTER JOIN contact_list cl ON cl.list_id = l.id
            LEFT OUTER JOIN contact c ON cl.contact_id = c.id
        WHERE l.id = :id
        """.trimIndent()
    }

    fun all() = session.run(
        queryOf(ALL_QUERY).map { it.toInfoList() }.asList,
    )

    fun byId(id: Long) = session.run(
        queryOf(
            statement = BY_ID_QUERY,
            paramMap = mapOf("id" to id),
        ).map { it.toInfoList(withContacts = true) }.asList,
    ).mergeFold { p1, p2 ->
        p1.copy(contacts = p1.contacts + p2.contacts)
    }.mergeFold { p1, p2 ->
        p1.copy(unsubscribed = p1.unsubscribed + p2.unsubscribed)
    }.firstOrNull()
}
