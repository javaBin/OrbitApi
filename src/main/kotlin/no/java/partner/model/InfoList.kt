package no.java.partner.model

data class InfoList(
    val id: Long,
    val name: String,

    val contacts: List<Contact>,
)
