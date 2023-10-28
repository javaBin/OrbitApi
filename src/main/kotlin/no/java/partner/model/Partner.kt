package no.java.partner.model

data class Partner(
    val id: Long,
    val name: String,
    val domainName: String?,

    val contacts: List<Contact>,
)
