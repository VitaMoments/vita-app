@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.user

import eu.vitamoments.app.data.enums.UserRole
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class UserDto(
    val uuid: Uuid,
    val username: String,
    val email: String,
    val alias: String? = null,
    val bio: String? = null,
    val role: UserRole = UserRole.User,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
    val imageUrl: String? = null
)
