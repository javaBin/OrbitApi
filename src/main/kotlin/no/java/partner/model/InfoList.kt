package no.java.partner.model

data class InfoList(
    override val id: Long,
    val name: String,

    val contacts: List<Contact>,
    val unsubscribed: List<Contact>,
) : Identifiable
