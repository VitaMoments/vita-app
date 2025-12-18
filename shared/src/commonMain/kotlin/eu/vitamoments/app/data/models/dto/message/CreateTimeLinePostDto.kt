package eu.vitamoments.app.data.models.dto.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CreateTimeLinePostDto(
    val content: JsonObject
)
