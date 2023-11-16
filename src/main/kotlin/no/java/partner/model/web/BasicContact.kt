package no.java.partner.model.web

import no.java.partner.model.Contact

data class BasicContact(
    val id: Long,
    val name: String?,
    val email: String,
    val telephone: String?,
    val source: String?,
)

fun Contact.toBasicContact() = BasicContact(this.id, this.name, this.email, this.telephone, this.source)
