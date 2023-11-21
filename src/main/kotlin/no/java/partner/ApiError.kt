package no.java.partner

import io.ktor.http.HttpStatusCode

sealed class ApiError(val statusCode: HttpStatusCode, val message: String)

data object PartnerNotFound : ApiError(
    HttpStatusCode.NotFound,
    "Partner not found",
)

data object ListNotFound : ApiError(
    HttpStatusCode.NotFound,
    "List not found",
)

data object ContactNotFound : ApiError(
    HttpStatusCode.NotFound,
    "Contact not found",
)

data object MissingID : ApiError(
    HttpStatusCode.BadRequest,
    "ID Parameter missing",
)

data object MissingPrincipal : ApiError(HttpStatusCode.Unauthorized, "Missing Principal")

data object NonAdminLogin : ApiError(HttpStatusCode.Unauthorized, "Not an admin")

data class GithubCallFailed(val upstreamStatusCode: HttpStatusCode, val upstreamMessage: String) : ApiError(
    HttpStatusCode.InternalServerError,
    "call to github failed",
)
