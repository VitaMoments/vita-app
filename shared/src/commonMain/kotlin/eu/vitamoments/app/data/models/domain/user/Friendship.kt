package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.enums.FriendshipStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

sealed interface Friendship {
    val id: Uuid
    val status: FriendshipStatus
    val direction: FriendshipDirection
    val createdAt: Instant
    val updatedAt: Instant
}
