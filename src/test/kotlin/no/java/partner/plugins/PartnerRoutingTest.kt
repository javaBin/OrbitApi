package no.java.partner.plugins

import arrow.core.right
import com.typesafe.config.ConfigFactory
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.java.partner.app
import no.java.partner.model.Contact
import no.java.partner.model.InfoList
import no.java.partner.model.Partner
import no.java.partner.model.web.BasicPartner
import no.java.partner.model.web.PartnerWithContacts
import no.java.partner.service.PartnerService
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class PartnerRoutingTest {
    @Test
    fun `GET partner returns partner list`() {
        val partnerService = mockk<PartnerService>()

        every { partnerService.allPartners() } returns listOf(
            Partner(id = 1, name = "Test Partner 1", domainName = "test.domain.tld", contacts = emptyList()),
        )

        testApplication {
            val client = setup(partnerService)

            val response = client.get("/partner") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.OK

            val partners = response.body<List<BasicPartner>>()

            partners.size shouldBe 1
            partners.first().let {
                it.id shouldBe 1L
                it.name shouldBe "Test Partner 1"
                it.domainName shouldBe "test.domain.tld"
            }
        }
    }

    @Test
    fun `GET partner 1 returns partner`() {
        val partnerService = mockk<PartnerService>()

        val slot = slot<Long>()

        every { partnerService.partnerById(capture(slot)) } returns Partner(
            id = 1,
            name = "Test Partner 1",
            domainName = "test.domain.tld",
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
        ).right()

        testApplication {
            val client = setup(partnerService)

            val response = client.get("/partner/1") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.OK

            slot.captured shouldBe 1L

            val partner = response.body<PartnerWithContacts>()

            partner.id shouldBe 1L
            partner.name shouldBe "Test Partner 1"
            partner.domainName shouldBe "test.domain.tld"

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
