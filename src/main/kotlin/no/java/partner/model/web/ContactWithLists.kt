package no.java.partner.model.web

import no.java.partner.model.Contact

data class ContactWithLists(
    val id: Long,
    val name: String?,
    val email: String,
    val telephone: String?,
    val source: String?,

    val lists: List<BasicInfoList>,
)

fun Contact.toContactWithLists() =
    ContactWithLists(
        this.id,
        this.name,
        this.email,
        this.telephone,
        this.source,
        this.lists.map { it.toBasicInfoList() },
    )
