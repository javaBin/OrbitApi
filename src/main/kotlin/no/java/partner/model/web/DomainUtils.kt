package no.java.partner.model.web

fun String?.toList() = this?.split('%') ?: emptyList()

fun List<String>.toSingle() = this.takeIf { this.isNotEmpty() }?.joinToString("%")
