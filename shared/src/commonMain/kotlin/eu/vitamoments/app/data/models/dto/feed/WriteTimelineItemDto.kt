package eu.vitamoments.app.data.models.dto.feed

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class WriteTimelineItemDto(
    val content: JsonObject
)
