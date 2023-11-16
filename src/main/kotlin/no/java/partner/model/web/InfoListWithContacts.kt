package no.java.partner.model.web

import no.java.partner.model.InfoList

data class InfoListWithContacts(
    val id: Long,
    val name: String,

    val contacts: List<BasicContact>,
    val unsubscribed: List<BasicContact>,
)

fun InfoList.toInfoListWithContacts() = InfoListWithContacts(
    this.id,
    this.name,
    this.contacts.map { it.toBasicContact() },
    this.unsubscribed.map { it.toBasicContact() },
)
