package no.java.partner.plugins.openapi

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.ktor.http.HttpStatusCode
import no.java.partner.model.web.BasicInfoList
import no.java.partner.model.web.BasicPartner
import no.java.partner.model.web.ContactWithLists
import no.java.partner.model.web.CreateContact
import no.java.partner.model.web.CreatePartner
import no.java.partner.model.web.PartnerWithContacts
import kotlin.reflect.typeOf

@Suppress("Duplicates")
object PartnerRoutingDoc {
    private val examplePartner = BasicPartner(id = 1L, name = "javaBin", domainName = listOf("java.no"))
    private val exampleCreatePartner = CreatePartner(name = "javaBin", domainName = listOf("java.no"))
    private val examplePartnerWithContacts = PartnerWithContacts(
        id = 1L,
        name = "javaBin",
        domainName = listOf("java.no"),
        contacts = listOf(
            ContactWithLists(
                id = 1L,
                name = "Duke",
                email = "duke@java.no",
                telephone = "12345678",
                source = "Example",
                lists = listOf(
                    BasicInfoList(id = 1L, name = "Info"),
                ),
            ),
        ),
    )
    private val exampleCreateContact =
        CreateContact(name = "Duke", email = "duke@java.no", telephone = "12345678", source = "Example")

    val partnerList =
        GetInfo.builder {
            summary("Partner List")
            description("Fetch all partners")
            response {
                responseType(typeOf<List<BasicPartner>>())
                responseCode(HttpStatusCode.OK)
                description("List of partners")
                examples(listOf(examplePartner).toExample())
            }
            canRespond(listOf(UnauthorizedResponseInfo, InternalServerErrorResponseInfo))
            tags("Partners")
        }

    val createPartner =
        PostInfo.builder {
            summary("Create Partner")
            description("Create a partner")
            request {
                requestType(typeOf<CreatePartner>())
                description("Partner to create")
                examples(exampleCreatePartner.toExample())
            }
            response {
                responseType(typeOf<BasicPartner>())
                responseCode(HttpStatusCode.OK)
                description("Newly created partner")
                examples(examplePartner.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Partners")
        }

    val partnerById =
        GetInfo.builder {
            summary("Partner By ID")
            description("Fetch a single partner")
            parameters("id".pathParam())
            response {
                responseType(typeOf<PartnerWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("Partner")
                examples(listOf(examplePartnerWithContacts).toExample())
            }
            canRespond(listOf(UnauthorizedResponseInfo, NotFoundResponseInfo, InternalServerErrorResponseInfo))
            tags("Partners")
        }

    val createContact =
        PostInfo.builder {
            summary("Create Contact")
            description("Create a contact for a partner")
            request {
                requestType(typeOf<CreateContact>())
                description("Contact to create")
                parameters("id".pathParam())
                examples(exampleCreateContact.toExample())
            }
            response {
                responseType(typeOf<PartnerWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("Updated partner")
                examples(examplePartnerWithContacts.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Partners")
        }
}
