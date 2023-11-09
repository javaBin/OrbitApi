package no.java.partner.model.web

data class CreateContact(
    val name: String,
    val email: String,
    val telephone: String?,
    val source: String?
)
