package no.java.partner.repository

import kotliquery.Row
import no.java.partner.model.Contact
import no.java.partner.model.Identifiable
import no.java.partner.model.InfoList
import no.java.partner.model.Partner

fun <T : Identifiable> List<T>.mergeFold(copy: (item1: T, item2: T) -> T) = this.fold(listOf<T>()) { acc, item ->
    val existing = acc.find { it.id == item.id }
    if (existing != null) {
        acc.filter { it.id != item.id } + copy(item, existing)
    } else {
        acc + item
    }
}

fun Row.toInfoListBasic() = InfoList(
    id = this.long("l_id"),
    name = this.string("l_name"),
    contacts = emptyList(),
    unsubscribed = emptyList(),
)

fun Row.toPartnerBasic() = Partner(
    id = this.long("p_id"),
    name = this.string("p_name"),
    domainName = this.stringOrNull("p_domainName"),
    contacts = emptyList(),
)

fun Row.toContactBasic() = Contact(
    id = this.long("c_id"),
    name = this.stringOrNull("c_name"),
    email = this.string("c_email"),
    telephone = this.stringOrNull("c_tel"),
    source = this.stringOrNull("c_source"),
    lists = emptyList(),
)

fun Row.toInfoList(withContacts: Boolean = false, withLists: Boolean = false) = this.toInfoListBasic().let { infoList ->
    if (withContacts && this.longOrNull("c_id") != null) {
        if (this.boolean("cl_subscribed")) {
            infoList.copy(contacts = listOf(this.toContact(withLists)))
        } else {
            infoList.copy(unsubscribed = listOf(this.toContact(withLists)))
        }
    } else {
        infoList
    }
}

fun Row.toPartner(withContacts: Boolean = false, withLists: Boolean = false) = this.toPartnerBasic().let { partner ->
    if (withContacts && this.longOrNull("c_id") != null) {
        partner.copy(contacts = listOf(this.toContact(withLists)))
    } else {
        partner
    }
}

fun Row.toContact(withLists: Boolean) = this.toContactBasic().let { contact ->
    if (withLists && this.longOrNull("l_id") != null) {
        contact.copy(lists = listOf(this.toInfoListBasic()))
    } else {
        contact
    }
}
