package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.FriendshipDirection
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("PENDING")
data class PendingFriendship(
    override val uuid: Uuid,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val deletedAt: Instant? = null,
    val direction: FriendshipDirection,
    override val otherUserId: Uuid,
) : Friendship {
    override val status: FriendshipStatus = FriendshipStatus.PENDING
}