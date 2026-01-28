package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.UserRole
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    @Contextual val createdAt: Instant,
    @Contextual val updatedAt: Instant,
    @Contextual val deletedAt: Instant?,
    override val imageUrl: String? = null
) : User
