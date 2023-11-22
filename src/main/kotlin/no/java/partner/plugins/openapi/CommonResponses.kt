package no.java.partner.plugins.openapi

import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private sealed class StandardApiResponseInfo(
    private val statusCode: HttpStatusCode,
    private val description: String,
    private val responseType: KType,
    private vararg val examples: Any,
) {

    fun build() = ResponseInfo.builder {
        responseCode(statusCode)
        description(description)
        responseType(responseType)
        @Suppress("SpreadOperator")
        examples(*(examples.mapIndexed { idx, example -> Pair("example-$idx", example) }.toTypedArray()))
    }
}

private data object BadRequestResponse : StandardApiResponseInfo(
    HttpStatusCode.BadRequest,
    "Bad Request - could not parse request",
    typeOf<String>(),
    "ID Parameter missing",
)

private data object NotFoundResponse : StandardApiResponseInfo(
    HttpStatusCode.NotFound,
    "Not Found - could not find something",
    typeOf<String>(),
    "Partner Not Found",
    "List Not Found",
    "Contact Not Found",
)

private data object UnauthorizedResponse : StandardApiResponseInfo(
    HttpStatusCode.Unauthorized,
    "Unauthorized - not logged in",
    typeOf<String>(),
    "Missing Principal",
    "Not an admin",
)

private data object InternalServerErrorResponse : StandardApiResponseInfo(
    HttpStatusCode.InternalServerError,
    "Internal Server Error - something went wrong",
    typeOf<String>(),
    "Something went wrong",
)

val BadRequestResponseInfo = BadRequestResponse.build()
val NotFoundResponseInfo = NotFoundResponse.build()
val UnauthorizedResponseInfo = UnauthorizedResponse.build()
val InternalServerErrorResponseInfo = InternalServerErrorResponse.build()
