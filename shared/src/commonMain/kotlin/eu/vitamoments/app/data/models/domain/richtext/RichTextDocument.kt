package eu.vitamoments.app.data.models.domain.richtext

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class RichTextDocument(
    val content: JsonObject
)
