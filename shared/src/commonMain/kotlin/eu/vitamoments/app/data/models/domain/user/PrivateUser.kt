package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Private User is the user provided to friends etc.
 */
@Serializable
@SerialName("PRIVATE")
data class PrivateUser(
    override val uuid: Uuid,
    val username: String,
    override val displayName: String = username,
    override val bio: String? = null,
    override val role: UserRole = UserRole.USER,
    val email: String,
    override var imageUrl: String? = null
) : User
