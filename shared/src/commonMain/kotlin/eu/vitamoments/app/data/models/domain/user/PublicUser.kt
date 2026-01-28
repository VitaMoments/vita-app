package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * PublicUser is the user provided when searching users etc.
 */
@Serializable
@SerialName("PUBLIC")
data class PublicUser(
    override val uuid: Uuid,
    override val displayName: String,
    override val bio: String? = null,
    override val imageUrl: String? = null,
    override val role: UserRole = UserRole.USER
) : User
