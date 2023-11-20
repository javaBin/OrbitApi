package no.java.partner.model.web

import no.java.partner.model.Partner

data class PartnerWithContacts(
    val id: Long,
    val name: String,
    val domainName: List<String>,

    val contacts: List<ContactWithLists>,
)

fun Partner.toPartnerWithContacts() =
    PartnerWithContacts(this.id, this.name, this.domainName.toList(), this.contacts.map { it.toContactWithLists() })
