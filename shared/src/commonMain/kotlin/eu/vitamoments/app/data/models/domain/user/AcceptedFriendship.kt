package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.FriendshipStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("ACCEPTED")
data class AcceptedFriendship(
    override val uuid: Uuid,
    override val otherUserId: Uuid,
    override val createdAt: Instant,
    override val updatedAt: Instant,
) : Friendship {
    override val deletedAt: Instant? = null
    override val status: FriendshipStatus = FriendshipStatus.ACCEPTED
}