package no.java.partner.model.web

import no.java.partner.model.NewPartner

data class CreatePartner(
    val name: String,
    val domainName: List<String>,
)

fun CreatePartner.toNewPartner() = NewPartner(this.name, this.domainName.toSingle())
