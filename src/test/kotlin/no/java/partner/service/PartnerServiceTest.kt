package no.java.partner.service

import arrow.core.left
import io.kotest.matchers.shouldBe
import no.java.partner.MissingID
import no.java.partner.PartnerNotFound
import no.java.partner.PostgresFunSpec
import no.java.partner.createContacts
import no.java.partner.createPartners
import no.java.partner.loadFixtures
import no.java.partner.repository.PartnerRepository

class PartnerServiceTest : PostgresFunSpec({ session ->

    val service = PartnerService(PartnerRepository(session))

    test("list partners returns all partners without contacts") {
        session.loadFixtures(listOf(createPartners, createContacts))

        val partners = service.allPartners()

        partners.size shouldBe 3
        partners.first().name shouldBe "Partner 1"
        partners.first().contacts.size shouldBe 0
    }

    test("partner by id returns partner with contacts") {
        session.loadFixtures(listOf(createPartners, createContacts))

        val possiblePartner = service.partnerById("1")

        possiblePartner.isRight() shouldBe true

        possiblePartner.getOrNull()?.let { partner ->
            partner.name shouldBe "Partner 1"
            partner.contacts.size shouldBe 4
            partner.contacts.find { it.id == 1L }?.let { contact ->
                contact.name shouldBe "Contact 1"
                contact.source shouldBe "Source 1"
                contact.partner?.id shouldBe partner.id
            }
        }
    }

    test("partner by id returns correct error if not found") {
        session.loadFixtures(listOf(createPartners, createContacts))

        val partner = service.partnerById("22")

        partner.isRight() shouldBe false

        partner shouldBe PartnerNotFound.left()
    }

    test("partner by id returns correct error if id is not valid") {
        session.loadFixtures(listOf(createPartners, createContacts))

        val partner = service.partnerById("x")

        partner.isRight() shouldBe false

        partner shouldBe MissingID.left()
    }
})
