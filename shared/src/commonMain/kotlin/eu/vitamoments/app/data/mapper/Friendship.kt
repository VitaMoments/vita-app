@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.user.AcceptedFriendship
import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PendingFriendship
import eu.vitamoments.app.data.models.dto.user.AcceptedFriendshipDto
import eu.vitamoments.app.data.models.dto.user.FriendshipDto
import eu.vitamoments.app.data.models.dto.user.PendingFriendshipDto
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

fun Friendship.toDto(): FriendshipDto = when (this) {
    is PendingFriendship -> this.toDto()
    is AcceptedFriendship -> this.toDto()
}

fun PendingFriendship.toDto(): PendingFriendshipDto =
    PendingFriendshipDto(
        uuid = this.id,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt.toEpochMilliseconds(),
        direction = this.direction,
        friend = this.friend.toDto()
    )

fun AcceptedFriendship.toDto(): AcceptedFriendshipDto =
    AcceptedFriendshipDto(
        uuid = this.id,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt.toEpochMilliseconds(),
        friend = this.friend.toDto()
    )
