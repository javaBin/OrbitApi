package no.java.partner.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import no.java.partner.ApiError
import no.java.partner.MissingID
import no.java.partner.PartnerNotFound
import no.java.partner.model.Partner
import no.java.partner.repository.PartnerRepository

class PartnerService(private val partnerRepository: PartnerRepository) {
    fun allPartners() = partnerRepository.all()

    fun partnerById(id: String?): Either<ApiError, Partner> {
        val partnerId = id?.toLongOrNull() ?: return MissingID.left()

        return partnerRepository.byId(partnerId)?.right() ?: PartnerNotFound.left()
    }
}
