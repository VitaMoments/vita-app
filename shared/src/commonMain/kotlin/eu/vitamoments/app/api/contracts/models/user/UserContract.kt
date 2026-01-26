package eu.vitamoments.app.api.contracts.models.user

import eu.vitamoments.app.api.contracts.models.friendship.FriendshipContract

import eu.vitamoments.app.data.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface UserContract {
    val uuid: Uuid
    val displayName: String
    val bio: String?
    val imageUrl: String?
}

@Serializable
@SerialName("PUBLIC")
data class PublicUserContract(
    override val uuid: Uuid,
    override val displayName: String,
    override val bio: String? = null,
    override val imageUrl: String? = null
) : UserContract

@Serializable
@SerialName("PRIVATE")
data class PrivateUserContract(
    override val uuid: Uuid,
    val username: String,
    val email: String,
    override val displayName: String = username,
    override val bio: String? = null,
    val role: UserRole = UserRole.USER,
    override val imageUrl: String? = null
) : UserContract

@Serializable
@SerialName("ACCOUNT")
data class AccountUserContract(
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
) : UserContract

@Serializable
@SerialName("CONTEXT")
data class UserWithContextContract(
    val user: UserContract,
    val friendship: FriendshipContract? = null,
) : UserContract by user