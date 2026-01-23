package eu.vitamoments.app.api.contracts.models.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class RichTextDocumentContract(
    val content: JsonObject
)