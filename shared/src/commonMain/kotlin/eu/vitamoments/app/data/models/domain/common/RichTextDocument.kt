package eu.vitamoments.app.data.models.domain.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RichTextDocument(
    val type: String? = null,
    val content: JsonElement? = null
)