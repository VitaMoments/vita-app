package eu.vitamoments.app.data.models.dto.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.models.dto.user.UserDto
import kotlin.uuid.Uuid

@Serializable
data class TimeLinePostDto(
    val uuid: Uuid,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    val content: JsonObject,
    val userDto: UserDto,
    val plainText: String,
    val html: String
)