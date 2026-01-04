package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.enums.FriendshipStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class PendingFriendship(
    override val id: Uuid,
    override val direction: FriendshipDirection,
    val friend: PublicUser,
    override val createdAt: Instant,
    override val updatedAt: Instant,
) : Friendship {
    override val status: FriendshipStatus = FriendshipStatus.PENDING
}