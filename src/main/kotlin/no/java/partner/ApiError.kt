package no.java.partner

import io.ktor.http.HttpStatusCode

sealed class ApiError(val statusCode: HttpStatusCode, val message: String)

data object PartnerNotFound : ApiError(
    HttpStatusCode.NotFound,
    "Partner not found"
)

data object MissingID : ApiError(
    HttpStatusCode.BadRequest,
    "ID Parameter missing"
)


// Add more as needed for example - if we have rules for passwords:

data class InvalidPassword(val validations: List<String>) : ApiError(
    HttpStatusCode.BadRequest,
    "Invalid password"
)