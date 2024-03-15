package no.java.partner.service

import arrow.core.Either
import arrow.core.flatten
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.partner.ApiError
import no.java.partner.ContactNotFound
import no.java.partner.MissingID
import no.java.partner.PartnerNotFound
import no.java.partner.model.NewPartner
import no.java.partner.model.Partner
import no.java.partner.model.web.CreateContact
import no.java.partner.repository.PartnerRepository

private val logger = KotlinLogging.logger {}

class PartnerService(private val partnerRepository: PartnerRepository) {
    fun allPartners() = partnerRepository.all()

    fun partnerById(id: Long?): Either<ApiError, Partner> {
        val partnerId = id ?: return MissingID.left()

        return partnerRepository.byId(partnerId)?.right() ?: PartnerNotFound.left()
    }

    fun createPartner(partner: NewPartner): Either<ApiError, Partner> {
        logger.info { "Creating Partner $partner" }

        val createPartner = partnerRepository.createPartner(partner)

        logger.debug { createPartner }

        return createPartner.let { id ->
            when (id) {
                null -> PartnerNotFound.left()
                else -> partnerById(id)
            }
        }
    }

    fun createContact(partnerId: Long?, contact: CreateContact) = either {
        logger.info { "Creating Contact $contact" }

        // Check that partner exists
        val partner = partnerById(partnerId).bind()

        logger.info { "Creating contact found partner $partner" }

        when (partnerRepository.createContact(partner.id, contact)) {
            null -> ContactNotFound.left()
            else -> partnerById(partner.id)
        }
    }.flatten()
}
