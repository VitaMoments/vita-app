package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.models.domain.user.AcceptedFriendship
import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PendingFriendship
import eu.vitamoments.app.data.models.dto.user.AcceptedFriendshipDto
import eu.vitamoments.app.data.models.dto.user.FriendshipDto
import eu.vitamoments.app.data.models.dto.user.PendingFriendshipDto
import kotlin.time.Instant

fun Friendship.toDto(): FriendshipDto = when (this) {
    is PendingFriendship -> this.toDto()
    is AcceptedFriendship -> this.toDto()
}

fun FriendshipDto.toDomain(): Friendship = when(this) {
    is PendingFriendshipDto -> this.toDomain()
    is AcceptedFriendshipDto -> this.toDomain()
}

fun PendingFriendship.toDto(): PendingFriendshipDto =
    PendingFriendshipDto(
        uuid = this.id,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt.toEpochMilliseconds(),
        direction = this.direction,
        otherUserId = this.otherUserId
    )

fun AcceptedFriendship.toDto(): AcceptedFriendshipDto =
    AcceptedFriendshipDto(
        uuid = this.id,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt.toEpochMilliseconds(),
        otherUserId = this.otherUserId
    )

fun PendingFriendshipDto.toDomain() : PendingFriendship = PendingFriendship(
    id = this.uuid,
    direction = this.direction,
    otherUserId = this.otherUserId,
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt)
)

fun AcceptedFriendshipDto.toDomain(): AcceptedFriendship = AcceptedFriendship(
    id = this.uuid,
    otherUserId = this.otherUserId,
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt)
)
