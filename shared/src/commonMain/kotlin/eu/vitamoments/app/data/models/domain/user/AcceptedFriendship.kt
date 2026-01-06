package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.enums.FriendshipStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid


data class AcceptedFriendship(
    override val id: Uuid,
    val otherUserId: Uuid,
    override val createdAt: Instant,
    override val updatedAt: Instant,
) : Friendship {
    override val direction: FriendshipDirection = FriendshipDirection.BOTH
    override val status: FriendshipStatus = FriendshipStatus.ACCEPTED
}