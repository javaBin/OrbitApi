package no.java.partner.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import no.java.partner.ApiError
import no.java.partner.ListNotFound
import no.java.partner.MissingID
import no.java.partner.model.InfoList
import no.java.partner.repository.ListRepository

class ListService(private val listRepository: ListRepository) {
    fun allLists() = listRepository.all()

    fun listById(id: Long?): Either<ApiError, InfoList> {
        val listId = id ?: return MissingID.left()

        return listRepository.byId(listId)?.right() ?: ListNotFound.left()
    }
}
