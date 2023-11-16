package no.java.partner.model.web

import no.java.partner.model.InfoList

data class BasicInfoList(
    val id: Long,
    val name: String,
)

fun InfoList.toBasicInfoList() = BasicInfoList(this.id, this.name)
