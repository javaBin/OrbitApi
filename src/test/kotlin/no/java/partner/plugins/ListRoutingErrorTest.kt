package no.java.partner.plugins

import arrow.core.left
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
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
import no.java.partner.ListNotFound
import no.java.partner.app
import no.java.partner.env
import no.java.partner.model.web.CreateInfoList
import no.java.partner.service.ListService
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class ListRoutingErrorTest {
    @Test
    fun `GET list 1 returns correct error when not found`() {
        val listService = mockk<ListService>()

        val idSlot = slot<Long>()

        every { listService.listById(capture(idSlot)) } returns ListNotFound.left()

        testApplication {
            val client = setup(listService)

            val response = client.get("/list/1") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.NotFound

            idSlot.captured shouldBe 1L

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "List not found"
        }
    }

    @Test
    fun `POST list returns correct error if partner not able to be created`() {
        val listService = mockk<ListService>()

        val listSlot = slot<CreateInfoList>()

        every { listService.createList(capture(listSlot)) } returns ListNotFound.left()

        testApplication {
            val client = setup(listService)

            val response = client.post("/list") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(ListRoutingTest.testCreateList)
            }

            response.status shouldBe HttpStatusCode.NotFound

            listSlot.captured shouldBe ListRoutingTest.testCreateList

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "List not found"
        }
    }

    @Test
    fun `POST contact returns correct error if list not found`() {
        val listService = mockk<ListService>()

        val listIdSlot = slot<Long>()
        val contactIdSlot = slot<Long>()

        every {
            listService.createSubscription(
                capture(listIdSlot),
                capture(contactIdSlot),
            )
        } returns ListNotFound.left()

        testApplication {
            val client = setup(listService)

            val response = client.post("/list/1/contact/2") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.NotFound

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "List not found"
        }
    }

    @Test
    fun `PATCH subscribe returns correct error if list not found`() {
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
        } returns ListNotFound.left()

        testApplication {
            val client = setup(listService)

            val response = client.patch("/list/1/contact/2/subscribe") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.NotFound

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L
            subscribeSlot.captured shouldBe true

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "List not found"
        }
    }

    @Test
    fun `PATCH unsubscribe returns correct error if list not found`() {
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
        } returns ListNotFound.left()

        testApplication {
            val client = setup(listService)

            val response = client.patch("/list/1/contact/2/unsubscribe") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.NotFound

            listIdSlot.captured shouldBe 1L
            contactIdSlot.captured shouldBe 2L
            subscribeSlot.captured shouldBe false

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "List not found"
        }
    }

    private fun ApplicationTestBuilder.setup(listService: ListService): HttpClient {
        env()

        app {
            configureListRouting(listService)
        }

        return testClient()
    }
}
