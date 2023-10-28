package no.java.partner.model

data class Contact(
    val id: Long,
    val name: String?,
    val email: String,
    val telephone: String?,

    val partner: Partner?,
    val lists: List<InfoList>
)
