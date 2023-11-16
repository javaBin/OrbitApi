package no.java.partner.service

import arrow.core.left
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import no.java.partner.ListNotFound
import no.java.partner.MissingID
import no.java.partner.PostgresFunSpec
import no.java.partner.createContacts
import no.java.partner.createListContacts
import no.java.partner.createLists
import no.java.partner.createPartners
import no.java.partner.loadFixtures
import no.java.partner.model.web.CreateInfoList
import no.java.partner.repository.ListRepository

class ListServiceTest : PostgresFunSpec({ session ->

    val service = ListService(ListRepository(session))

    test("list lists returns all lists without contacts") {
        session.loadFixtures(listOf(createPartners, createContacts, createLists, createListContacts))

        val lists = service.allLists()

        lists.size shouldBe 3
        lists.first().name shouldBe "List 1"
        lists.first().contacts.size shouldBe 0
    }

    test("list by id returns list with contacts") {
        session.loadFixtures(listOf(createPartners, createContacts, createLists, createListContacts))

        val possibleList = service.listById(1L)

        possibleList.isRight() shouldBe true

        possibleList.getOrNull()?.let { infoList ->
            infoList.name shouldBe "List 1"
            infoList.contacts.size shouldBe 2
            infoList.contacts.find { it.id == 1L }?.let { contact ->
                contact.name shouldBe "Contact 1"
                contact.source shouldBe "Source 1"
            }
        }
    }

    test("list by id returns correct error if not found") {
        session.loadFixtures(listOf(createPartners, createContacts, createLists, createListContacts))

        val list = service.listById(22L)

        list.isRight() shouldBe false

        list shouldBe ListNotFound.left()
    }

    test("list by id returns correct error if id is not valid") {
        session.loadFixtures(listOf(createPartners, createContacts, createLists, createListContacts))

        val list = service.listById(null)

        list.isRight() shouldBe false

        list shouldBe MissingID.left()
    }

    test("create list creates a list") {
        val infoList = service.createList(CreateInfoList("Test Create List"))

        infoList.isRight() shouldBe true

        infoList.getOrNull()?.let {
            it.name shouldBe "Test Create List"
            it.id shouldBeGreaterThan 0L
        }
    }
})
