package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.enums.FriendshipStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid


data class AcceptedFriendship(
    override val id: Uuid,
    override val direction: FriendshipDirection,
    val friend: PrivateUser,
    override val createdAt: Instant,
    override val updatedAt: Instant,
) : Friendship {
    override val status: FriendshipStatus = FriendshipStatus.ACCEPTED
}