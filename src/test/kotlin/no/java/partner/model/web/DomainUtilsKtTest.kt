package no.java.partner.model.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DomainUtilsKtTest : FunSpec({

    test("null domain gives empty list") {
        val domainList = null.toList()

        domainList shouldBe emptyList()
    }

    test("single domain gives single entry list") {
        val domainList = "test1.domain.tld".toList()

        domainList shouldBe listOf("test1.domain.tld")
    }

    test("multiple domains gives list") {
        val domainList = "test1.domain.tld%test2.domain.tld%test3.domain.tld".toList()

        domainList shouldBe listOf("test1.domain.tld", "test2.domain.tld", "test3.domain.tld")
    }

    test("empty list gives null domain") {
        val domain = emptyList<String>().toSingle()

        domain shouldBe null
    }

    test("single entry list gives single domain") {
        val domain = listOf("test1.domain.tld").toSingle()

        domain shouldBe "test1.domain.tld"
    }

    test("list gives multiple domains") {
        val domain = listOf("test1.domain.tld", "test2.domain.tld", "test3.domain.tld").toSingle()

        domain shouldBe "test1.domain.tld%test2.domain.tld%test3.domain.tld"
    }
})
