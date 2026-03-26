package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.domain.media.MediaAsset
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.enums.UserRole
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
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
    val email: String? = null,
    override var profileImageAsset: MediaAsset? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val phone: String? = null,
    @Serializable(with = InstantSerializer::class) val birthDate: Instant? = null,
    override val coverImageAsset: MediaAsset? = null,
    val locale: String? = null,
    val timeZone: String? = null,
    val privacyDetails: PrivacyStatus = PrivacyStatus.PRIVATE
) : User
