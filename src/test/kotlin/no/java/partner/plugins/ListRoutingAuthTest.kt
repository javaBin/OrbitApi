package no.java.partner.plugins

import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
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
import io.mockk.mockk
import no.java.partner.app
import no.java.partner.env
import no.java.partner.testClient
import org.junit.jupiter.api.Test

class ListRoutingAuthTest {

    @Test
    fun `GET list without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.get("/list") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `GET list 1 without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.get("/list/1") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `POST list without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.post("/list") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(ListRoutingTest.testCreateList)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `POST contact without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.post("/list/1/contact/2") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `PATCH contact subscribe without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.patch("/list/1/contact/2/subscribe") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `PATCH contact unsubscribe without token returns 401`() {
        testApplication {
            val client = setup()

            val response = client.patch("/list/1/contact/2/unsubscribe") {
                accept(ContentType.Application.Json)
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    private fun ApplicationTestBuilder.setup(): HttpClient {
        env()

        app {
            configureListRouting(mockk())
        }

        return testClient()
    }
}
