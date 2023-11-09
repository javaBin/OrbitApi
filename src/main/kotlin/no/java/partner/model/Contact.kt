package no.java.partner.model

data class Contact(
    override val id: Long,
    val name: String?,
    val email: String,
    val telephone: String?,
    val source: String?,

    val partner: Partner?,
    val lists: List<InfoList>,
) : Identifiable
