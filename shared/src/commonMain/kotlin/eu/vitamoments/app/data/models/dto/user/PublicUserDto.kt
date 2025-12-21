@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.user

import eu.vitamoments.app.data.enums.UserRole
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class PublicUserDto(
    val uuid: Uuid,
    val username: String,
    val alias: String? = null,
    val bio: String? = null,
    val role: UserRole = UserRole.User,
    val email: String,
    val imageUrl: String? = null
)
