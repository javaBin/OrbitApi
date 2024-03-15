package no.java.partner.service

import arrow.core.Either
import arrow.core.flatten
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import io.github.oshai.kotlinlogging.KotlinLogging
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

    fun createSubscription(listId: Long?, contactId: Long?): Either<ApiError, InfoList> = either {
        logger.info { "Creating Subscription $listId $contactId" }

        val list = listById(listId).bind()

        when (contactId) {
            null -> MissingID.left()
            else -> {
                listRepository.createSubscription(list.id, contactId)
                listById(list.id)
            }
        }
    }.flatten()

    fun updateSubscription(listId: Long?, contactId: Long?, subscription: Boolean): Either<ApiError, InfoList> =
        either {
            logger.info { "Updating Subscription $listId $contactId $subscription" }

            val list = listById(listId).bind()

            when (contactId) {
                null -> MissingID.left()
                else -> {
                    listRepository.updateSubscription(list.id, contactId, subscription)
                    listById(list.id)
                }
            }
        }.flatten()
}
