package eu.vitamoments.app.data.models.domain.friendship

import eu.vitamoments.app.data.models.enums.FriendshipDirection
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("PENDING")
data class PendingFriendship(
    override val uuid: Uuid,
    @Serializable(with = InstantSerializer::class) override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) override val updatedAt: Instant,
    @Serializable(with = InstantSerializer::class) override val deletedAt: Instant? = null,
    val direction: FriendshipDirection,
    override val otherUserId: Uuid,
) : Friendship {
    override val status: FriendshipStatus = FriendshipStatus.PENDING
}