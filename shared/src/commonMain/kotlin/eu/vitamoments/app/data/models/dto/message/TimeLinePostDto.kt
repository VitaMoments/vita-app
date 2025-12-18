@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.models.dto.user.PublicUserDto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TimeLinePostDto(
    val uuid: Uuid,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    val content: JsonObject,
    val userDto: PublicUserDto,
    val plainText: String,
    val html: String
)