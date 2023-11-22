package no.java.partner.plugins.openapi

import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter

fun String.pathParam() = Parameter(
    name = this,
    `in` = Parameter.Location.path,
    schema = TypeDefinition.STRING,
)

fun Any.toExample() = Pair(
    "example",
    this,
)
