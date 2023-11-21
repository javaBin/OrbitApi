package no.java.partner.plugins

import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import no.java.partner.app
import no.java.partner.env
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class PartnerRoutingAuthTest {

    @Test
    fun `GET partner without token gives 401`() {
        testApplication {
            val client = setup()

            val response = client.get("/partner") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `GET partner 1 without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.get("/partner/1") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `POST partner without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.post("/partner") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(PartnerRoutingTest.testCreatePartner)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `POST contact without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.post("/partner/1/contact") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(PartnerRoutingTest.testCreateContact)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    private fun ApplicationTestBuilder.setup(): HttpClient {
        env()

        app {
            configurePartnerRouting(mockk())
        }

        return testClient()
    }
}
