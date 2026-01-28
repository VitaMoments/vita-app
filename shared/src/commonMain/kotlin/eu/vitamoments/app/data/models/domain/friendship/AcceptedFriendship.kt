package eu.vitamoments.app.data.models.domain.friendship

import eu.vitamoments.app.data.models.domain.friendship.Friendship
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("ACCEPTED")
data class AcceptedFriendship(
    override val uuid: Uuid,
    override val otherUserId: Uuid,
    @Serializable(with = InstantSerializer::class) override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) override val updatedAt: Instant,
) : Friendship {
    @Serializable(with = InstantSerializer::class) override val deletedAt: Instant? = null
    override val status: FriendshipStatus = FriendshipStatus.ACCEPTED
}