package no.java.partner.model.web

import no.java.partner.model.Partner

data class BasicPartner(
    val id: Long,
    val name: String,
    val domainName: List<String>,
)

fun Partner.toBasicPartner() = BasicPartner(this.id, this.name, this.domainName.toList())
