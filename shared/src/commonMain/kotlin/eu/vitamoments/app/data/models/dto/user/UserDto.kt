@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.user

import eu.vitamoments.app.data.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed interface UserDto {
    val uuid: Uuid
    val displayName: String
    val bio: String?
    val imageUrl: String?
}

@Serializable
@SerialName("PUBLIC")
data class PublicUserDto(
    override val uuid: Uuid,
    override val displayName: String,
    override val bio: String? = null,
    override val imageUrl: String? = null
) : UserDto

@Serializable
@SerialName("PRIVATE")
data class PrivateUserDto(
    override val uuid: Uuid,
    val username: String,
    val email: String,
    override val displayName: String = username,
    override val bio: String? = null,
    val role: UserRole = UserRole.USER,
    override val imageUrl: String? = null
) : UserDto

@Serializable
@SerialName("ACCOUNT")
data class AccountUserDto(
    override val uuid: Uuid,
    val username: String,
    val email: String,
    val alias: String? = null,
    override val displayName: String = username,
    override val bio: String? = null,
    val role: UserRole = UserRole.USER,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    override val imageUrl: String? = null
) : UserDto