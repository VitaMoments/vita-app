package eu.vitamoments.app.data.models.domain.friendship

import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
sealed interface Friendship {
    val uuid: Uuid
    val otherUserId: Uuid
    val status: FriendshipStatus
    @Serializable(with = InstantSerializer::class) val createdAt: Instant
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant
    @Serializable(with = InstantSerializer::class) val deletedAt: Instant?
}