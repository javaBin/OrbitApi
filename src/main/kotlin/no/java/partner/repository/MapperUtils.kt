package no.java.partner.repository

import no.java.partner.model.Identifiable

fun <T : Identifiable> List<T>.mergeFold(copy: (item1: T, item2: T) -> T) = this.fold(listOf<T>()) { acc, item ->
    val existing = acc.find { it.id == item.id }
    if (existing != null) {
        acc.filter { it.id != item.id } + copy(item, existing)
    } else {
        acc + item
    }
}
