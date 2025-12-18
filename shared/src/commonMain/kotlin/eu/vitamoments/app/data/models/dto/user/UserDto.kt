@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class UserDto(
    val uuid: Uuid,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
    val imageUrl: String? = null
)
