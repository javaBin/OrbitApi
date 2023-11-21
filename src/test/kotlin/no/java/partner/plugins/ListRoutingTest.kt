package no.java.partner.plugins

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.java.partner.app
import no.java.partner.buildTestToken
import no.java.partner.env
import no.java.partner.model.Contact
import no.java.partner.model.InfoList
import no.java.partner.model.web.BasicInfoList
import no.java.partner.model.web.CreateInfoList
import no.java.partner.model.web.InfoListWithContacts
import no.java.partner.service.ListService
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class ListRoutingTest {

    @Test
    fun `GET list returns lists`() {
        val listService = mockk<ListService>()

        every { listService.allLists() } returns listOf(testList)

        testApplication {
            val client = setup(listService)

            val response = client.get("/list") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            val lists = response.body<List<BasicInfoList>>()

            lists.size shouldBe 1
            lists.first().let {
                it.id shouldBe 1L
                it.name shouldBe "Test List 1"
            }
        }
    }

    @Test
    fun `GET list 1 returns list`() {
        val listService = mockk<ListService>()

        val idSlot = slot<Long>()

        every { listService.listById(capture(idSlot)) } returns testList.right()

        testApplication {
            val client = setup(listService)

            val response = client.get("/list/1") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            idSlot.captured shouldBe 1L

            val list = response.body<InfoListWithContacts>()

            list.id shouldBe 1L
            list.name shouldBe "Test List 1"

            list.contacts.size shouldBe 1
            list.contacts.first().let { contact ->
                contact.id shouldBe 1L
                contact.name shouldBe "Test Contact 1"
                contact.email shouldBe "test@domain.tld"
                contact.telephone shouldBe "Test Telephone"
                contact.source shouldBe "Test Source"
            }

            list.unsubscribed.size shouldBe 1
            list.unsubscribed.first().let { contact ->
                contact.id shouldBe 2L
                contact.name shouldBe "Test Contact 2"
                contact.email shouldBe "test2@domain.tld"
                contact.telephone shouldBe "Test Telephone 2"
                contact.source shouldBe "Test Source 2"
            }
        }
    }

    @Test
    fun `POST list creates list`() {
        val listService = mockk<ListService>()

        val listSlot = slot<CreateInfoList>()

        every { listService.createList(capture(listSlot)) } returns testList.right()

        testApplication {
            val client = setup(listService)

            val response = client.post("/list") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
                setBody(testCreateList)
            }

            response.status shouldBe HttpStatusCode.OK

            listSlot.captured shouldBe testCreateList

            val list = response.body<BasicInfoList>()

            list.id shouldBe 1L
            list.name shouldBe "Test List 1"
        }
    }

    @Test
    fun `POST contact creates contact connection`() {
        val listService = mockk<ListService>()

        val listIdSlot = slot<Long>()
        val contactIdSlot = slot<Long>()

        every { listService.createSubscription(capture(listIdSlot), capture(contactIdSlot)) } returns testList.right()

        testApplication {
            val client = setup(listService)

            val response = client.post("/list/1/contact/2") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L

            val list = response.body<InfoListWithContacts>()

            list.id shouldBe 1L
            list.name shouldBe "Test List 1"
        }
    }

    @Test
    fun `PATCH contact subscribes`() {
        val listService = mockk<ListService>()

        val listIdSlot = slot<Long>()
        val contactIdSlot = slot<Long>()
        val subscribeSlot = slot<Boolean>()

        every {
            listService.updateSubscription(
                capture(listIdSlot),
                capture(contactIdSlot),
                capture(subscribeSlot),
            )
        } returns testList.right()

        testApplication {
            val client = setup(listService)

            val response = client.patch("/list/1/contact/2/subscribe") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L
            subscribeSlot.captured shouldBe true

            val list = response.body<InfoListWithContacts>()

            list.id shouldBe 1L
            list.name shouldBe "Test List 1"
        }
    }

    @Test
    fun `PATCH contact unsubscribes`() {
        val listService = mockk<ListService>()

        val listIdSlot = slot<Long>()
        val contactIdSlot = slot<Long>()
        val subscribeSlot = slot<Boolean>()

        every {
            listService.updateSubscription(
                capture(listIdSlot),
                capture(contactIdSlot),
                capture(subscribeSlot),
            )
        } returns testList.right()

        testApplication {
            val client = setup(listService)

            val response = client.patch("/list/1/contact/2/unsubscribe") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L
            subscribeSlot.captured shouldBe false

            val list = response.body<InfoListWithContacts>()

            list.id shouldBe 1L
            list.name shouldBe "Test List 1"
        }
    }

    private fun ApplicationTestBuilder.setup(listService: ListService): HttpClient {
        env()

        app {
            configureListRouting(listService)
        }

        return testClient()
    }

    companion object {
        val testCreateList = CreateInfoList(name = "Test List 1")

        val testList = InfoList(
            id = 1,
            name = "Test List 1",
            contacts = listOf(
                Contact(
                    id = 1L,
                    name = "Test Contact 1",
                    email = "test@domain.tld",
                    telephone = "Test Telephone",
                    source = "Test Source",
                    lists = emptyList(),
                ),
            ),
            unsubscribed = listOf(
                Contact(
                    id = 2L,
                    name = "Test Contact 2",
                    email = "test2@domain.tld",
                    telephone = "Test Telephone 2",
                    source = "Test Source 2",
                    lists = emptyList(),
                ),
            ),
        )
    }
}
