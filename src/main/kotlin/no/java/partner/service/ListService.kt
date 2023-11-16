package no.java.partner.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import mu.KotlinLogging
import no.java.partner.ApiError
import no.java.partner.ListNotFound
import no.java.partner.MissingID
import no.java.partner.model.InfoList
import no.java.partner.model.web.CreateInfoList
import no.java.partner.repository.ListRepository

private val logger = KotlinLogging.logger {}

class ListService(private val listRepository: ListRepository) {
    fun allLists() = listRepository.all()

    fun listById(id: Long?): Either<ApiError, InfoList> {
        val listId = id ?: return MissingID.left()

        return listRepository.byId(listId)?.right() ?: ListNotFound.left()
    }

    fun createList(list: CreateInfoList): Either<ApiError, InfoList> {
        logger.info { "Creating List $list" }

        val createList = listRepository.createList(list)

        logger.debug { createList }

        return createList.let { id ->
            when (id) {
                null -> ListNotFound.left()
                else -> listById(id)
            }
        }
    }
}
