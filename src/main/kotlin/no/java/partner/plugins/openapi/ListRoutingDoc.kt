package no.java.partner.plugins.openapi

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.ktor.http.HttpStatusCode
import no.java.partner.model.web.BasicContact
import no.java.partner.model.web.BasicInfoList
import no.java.partner.model.web.CreateInfoList
import no.java.partner.model.web.InfoListWithContacts
import kotlin.reflect.typeOf

@Suppress("Duplicates")
object ListRoutingDoc {
    private val exampleList = BasicInfoList(id = 1L, name = "Info")
    private val exampleCreateList = CreateInfoList(name = "Info")
    private val exampleListWithContacts = InfoListWithContacts(
        id = 1L,
        name = "Info",
        contacts = listOf(
            BasicContact(id = 1L, name = "Duke", email = "duke@java.no", telephone = "12345678", source = "Example"),
        ),
        unsubscribed = emptyList(),
    )
    private val exampleListWithContactsUnsubscribed = InfoListWithContacts(
        id = 1L,
        name = "Info",
        contacts = emptyList(),
        unsubscribed = listOf(
            BasicContact(id = 1L, name = "Duke", email = "duke@java.no", telephone = "12345678", source = "Example"),
        ),
    )

    val listList =
        GetInfo.builder {
            summary("List List")
            description("Fetch all lists")
            response {
                responseType(typeOf<List<BasicInfoList>>())
                responseCode(HttpStatusCode.OK)
                description("List of lists")
                examples(listOf(exampleList).toExample())
            }
            canRespond(listOf(UnauthorizedResponseInfo, InternalServerErrorResponseInfo))
            tags("Lists")
        }

    val createList =
        PostInfo.builder {
            summary("Create List")
            description("Create a List")
            request {
                requestType(typeOf<CreateInfoList>())
                description("List to create")
                examples(exampleCreateList.toExample())
            }
            response {
                responseType(typeOf<BasicInfoList>())
                responseCode(HttpStatusCode.OK)
                description("Newly created list")
                examples(exampleList.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Lists")
        }

    val listById =
        GetInfo.builder {
            summary("List By ID")
            description("Fetch a single list")
            response {
                responseType(typeOf<InfoListWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("List")
                examples(exampleListWithContacts.toExample())
            }
            canRespond(listOf(UnauthorizedResponseInfo, NotFoundResponseInfo, InternalServerErrorResponseInfo))
            tags("Lists")
        }

    val createSubscription =
        PostInfo.builder {
            summary("Create subscription")
            description("Subscribe to a list")
            parameters(
                "id".pathParam(),
                "contact".pathParam(),
            )
            response {
                responseType(typeOf<InfoListWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("Updated list")
                examples(exampleListWithContacts.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Lists")
        }

    val subscribe =
        PatchInfo.builder {
            summary("Subscribe")
            description("Update existing subscription - set subscribed")
            parameters(
                "id".pathParam(),
                "contact".pathParam(),
            )
            response {
                responseType(typeOf<InfoListWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("Updated list")
                examples(exampleListWithContacts.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Lists")
        }

    val unsubscribe =
        PatchInfo.builder {
            summary("Unsubscribe")
            description("Update existing subscription - set unsubscribed")
            parameters(
                "id".pathParam(),
                "contact".pathParam(),
            )
            response {
                responseType(typeOf<InfoListWithContacts>())
                responseCode(HttpStatusCode.OK)
                description("Updated list")
                examples(exampleListWithContactsUnsubscribed.toExample())
            }
            canRespond(
                listOf(
                    UnauthorizedResponseInfo,
                    BadRequestResponseInfo,
                    NotFoundResponseInfo,
                    InternalServerErrorResponseInfo,
                ),
            )
            tags("Lists")
        }
}
