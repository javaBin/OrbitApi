package no.java.partner.plugins

import arrow.core.left
import com.typesafe.config.ConfigFactory
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.java.partner.ContactNotFound
import no.java.partner.PartnerNotFound
import no.java.partner.app
import no.java.partner.model.web.CreateContact
import no.java.partner.model.web.CreatePartner
import no.java.partner.service.PartnerService
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class PartnerRoutingErrorTest {
    @Test
    fun `GET partner 1 returns correct error when not found`() {
        val partnerService = mockk<PartnerService>()

        val idSlot = slot<Long>()

        every { partnerService.partnerById(capture(idSlot)) } returns PartnerNotFound.left()

        testApplication {
            val client = setup(partnerService)

            val response = client.get("/partner/1") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.NotFound

            idSlot.captured shouldBe 1L

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "Partner not found"
        }
    }

    @Test
    fun `POST partner returns correct error if partner not able to be created`() {
        val partnerService = mockk<PartnerService>()

        val partnerSlot = slot<CreatePartner>()

        every { partnerService.createPartner(capture(partnerSlot)) } returns PartnerNotFound.left()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(PartnerRoutingTest.testCreatePartner)
            }

            response.status shouldBe HttpStatusCode.NotFound

            partnerSlot.captured shouldBe PartnerRoutingTest.testCreatePartner

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "Partner not found"
        }
    }

    @Test
    fun `POST contact returns correct error if partner not found`() {
        val partnerService = mockk<PartnerService>()

        val idSlot = slot<Long>()
        val contactSlot = slot<CreateContact>()

        every { partnerService.createContact(capture(idSlot), capture(contactSlot)) } returns PartnerNotFound.left()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner/1/contact") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(PartnerRoutingTest.testCreateContact)
            }

            response.status shouldBe HttpStatusCode.NotFound

            idSlot.captured shouldBe 1L
            contactSlot.captured shouldBe PartnerRoutingTest.testCreateContact

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "Partner not found"
        }
    }

    @Test
    fun `POST contact returns correct error if contact not able to be created`() {
        val partnerService = mockk<PartnerService>()

        val idSlot = slot<Long>()
        val contactSlot = slot<CreateContact>()

        every { partnerService.createContact(capture(idSlot), capture(contactSlot)) } returns ContactNotFound.left()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner/1/contact") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(PartnerRoutingTest.testCreateContact)
            }

            response.status shouldBe HttpStatusCode.NotFound

            idSlot.captured shouldBe 1L
            contactSlot.captured shouldBe PartnerRoutingTest.testCreateContact

            val message = response.body<ApiErrorResponse>()

            message.message shouldBe "Contact not found"
        }
    }

    private fun ApplicationTestBuilder.setup(partnerService: PartnerService): HttpClient {
        environment {
            config = HoconApplicationConfig(ConfigFactory.parseMap(emptyMap()))
        }

        app {
            configurePartnerRouting(partnerService)
        }

        return testClient()
    }
}
