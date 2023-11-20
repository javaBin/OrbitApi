package no.java.partner.model

data class Partner(
    override val id: Long,
    val name: String,
    val domainName: String?,

    val contacts: List<Contact>,
) : Identifiable

data class NewPartner(
    val name: String,
    val domainName: String?,
)
