package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.FriendshipStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
sealed interface Friendship {
    val uuid: Uuid
    val otherUserId: Uuid
    val status: FriendshipStatus
    @Contextual val createdAt: Instant
    @Contextual val updatedAt: Instant
    @Contextual val deletedAt: Instant?
}
