package no.java.partner.plugins

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
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
import no.java.partner.model.NewPartner
import no.java.partner.model.Partner
import no.java.partner.model.web.BasicPartner
import no.java.partner.model.web.CreateContact
import no.java.partner.model.web.CreatePartner
import no.java.partner.model.web.PartnerWithContacts
import no.java.partner.model.web.toNewPartner
import no.java.partner.service.PartnerService
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class PartnerRoutingTest {

    @Test
    fun `GET partner returns partner list`() {
        val partnerService = mockk<PartnerService>()

        every { partnerService.allPartners() } returns listOf(testPartner)

        testApplication {
            val client = setup(partnerService)

            val response = client.get("/partner") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            val partners = response.body<List<BasicPartner>>()

            partners.size shouldBe 1
            partners.first().let {
                it.id shouldBe 1L
                it.name shouldBe "Test Partner 1"
                it.domainName shouldBe listOf("test1.domain.tld", "test2.domain.tld")
            }
        }
    }

    @Test
    fun `GET partner 1 returns partner`() {
        val partnerService = mockk<PartnerService>()

        val idSlot = slot<Long>()

        every { partnerService.partnerById(capture(idSlot)) } returns testPartner.right()

        testApplication {
            val client = setup(partnerService)

            val response = client.get("/partner/1") {
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
            }

            response.status shouldBe HttpStatusCode.OK

            idSlot.captured shouldBe 1L

            val partner = response.body<PartnerWithContacts>()

            partner.id shouldBe 1L
            partner.name shouldBe "Test Partner 1"
            partner.domainName shouldBe listOf("test1.domain.tld", "test2.domain.tld")

            partner.contacts.size shouldBe 1
            partner.contacts.first().let { contact ->
                contact.id shouldBe 1L
                contact.name shouldBe "Test Contact 1"
                contact.email shouldBe "test@domain.tld"
                contact.telephone shouldBe "Test Telephone"
                contact.source shouldBe "Test Source"

                contact.lists.size shouldBe 1
                contact.lists.first().let { infoList ->
                    infoList.id shouldBe 1L
                    infoList.name shouldBe "Test List 1"
                }
            }
        }
    }

    @Test
    fun `POST partner creates partner`() {
        val partnerService = mockk<PartnerService>()

        val partnerSlot = slot<NewPartner>()

        every { partnerService.createPartner(capture(partnerSlot)) } returns testPartner.right()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
                setBody(testCreatePartner)
            }

            response.status shouldBe HttpStatusCode.OK

            partnerSlot.captured shouldBe testCreatePartner.toNewPartner()

            val partner = response.body<BasicPartner>()

            partner.id shouldBe 1L
            partner.name shouldBe "Test Partner 1"
            partner.domainName shouldBe listOf("test1.domain.tld", "test2.domain.tld")
        }
    }

    @Test
    fun `POST partner without domain creates partner`() {
        val partnerService = mockk<PartnerService>()

        val partnerSlot = slot<NewPartner>()

        every { partnerService.createPartner(capture(partnerSlot)) } returns testPartnerNoDomain.right()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
                setBody(testCreatePartnerNoDomain)
            }

            response.status shouldBe HttpStatusCode.OK

            partnerSlot.captured shouldBe testCreatePartnerNoDomain.toNewPartner()

            val partner = response.body<BasicPartner>()

            partner.id shouldBe 1L
            partner.name shouldBe "Test Partner 1"
            partner.domainName shouldBe emptyList()
        }
    }

    @Test
    fun `POST contact creates contact`() {
        val partnerService = mockk<PartnerService>()

        val idSlot = slot<Long>()
        val contactSlot = slot<CreateContact>()

        every { partnerService.createContact(capture(idSlot), capture(contactSlot)) } returns testPartner.right()

        testApplication {
            val client = setup(partnerService)

            val response = client.post("/partner/1/contact") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(buildTestToken())
                setBody(testCreateContact)
            }

            response.status shouldBe HttpStatusCode.OK

            idSlot.captured shouldBe 1L
            contactSlot.captured shouldBe testCreateContact

            val partner = response.body<PartnerWithContacts>()

            partner.id shouldBe 1L
            partner.name shouldBe "Test Partner 1"
            partner.domainName shouldBe listOf("test1.domain.tld", "test2.domain.tld")
        }
    }

    private fun ApplicationTestBuilder.setup(partnerService: PartnerService): HttpClient {
        env()

        app {
            configurePartnerRouting(partnerService)
        }

        return testClient()
    }

    companion object {
        val testCreatePartner =
            CreatePartner(name = "Test Partner", domainName = listOf("test1.domain.tld", "test2.domain.tld"))

        val testCreatePartnerNoDomain =
            CreatePartner(name = "Test Partner", domainName = emptyList())

        val testCreateContact = CreateContact(
            name = "Test Contact",
            email = "test@domain.tld",
            telephone = "Test Telephone",
            source = "Test Source",
        )

        val testPartner = Partner(
            id = 1,
            name = "Test Partner 1",
            domainName = "test1.domain.tld%test2.domain.tld",
            contacts = listOf(
                Contact(
                    id = 1L,
                    name = "Test Contact 1",
                    email = "test@domain.tld",
                    telephone = "Test Telephone",
                    source = "Test Source",
                    lists = listOf(
                        InfoList(
                            id = 1L,
                            name = "Test List 1",
                            contacts = emptyList(),
                            unsubscribed = emptyList(),
                        ),
                    ),
                ),
            ),
        )

        val testPartnerNoDomain = Partner(
            id = 1,
            name = "Test Partner 1",
            domainName = null,
            contacts = listOf(
                Contact(
                    id = 1L,
                    name = "Test Contact 1",
                    email = "test@domain.tld",
                    telephone = "Test Telephone",
                    source = "Test Source",
                    lists = listOf(
                        InfoList(
                            id = 1L,
                            name = "Test List 1",
                            contacts = emptyList(),
                            unsubscribed = emptyList(),
                        ),
                    ),
                ),
            ),
        )
    }
}
