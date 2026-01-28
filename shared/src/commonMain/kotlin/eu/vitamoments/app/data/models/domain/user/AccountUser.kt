package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.UserRole
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * AccountUser is provided when the signed in user is this user.
 */
@Serializable
@SerialName("ACCOUNT")
data class AccountUser(
    override val uuid: Uuid,
    val username: String,
    val email: String,
    val alias: String? = null,
    override val displayName: String = username,
    override val bio: String? = null,
    override val role: UserRole = UserRole.USER,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant,
    @Serializable(with = InstantSerializer::class) val deletedAt: Instant?,
    override val imageUrl: String? = null
) : User
