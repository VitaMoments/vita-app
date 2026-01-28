package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.FriendshipEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipDirection
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.friendship.AcceptedFriendship
import eu.vitamoments.app.data.models.domain.friendship.DeletedFriendship
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import eu.vitamoments.app.data.models.domain.friendship.PendingFriendship
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun FriendshipEntity.toDomain(userId: Uuid) : Friendship = when(status) {
        FriendshipStatus.PENDING -> this.toPendingDomain(userId)
        FriendshipStatus.ACCEPTED -> this.toAcceptedDomain(userId)
        FriendshipStatus.REMOVED -> this.toDeletedDomain(userId)
        else -> throw NotImplementedError("$status is not accepted")
    }

fun FriendshipEntity.toPendingDomain(userId: Uuid) : PendingFriendship {
    require(this.status == FriendshipStatus.PENDING) { "Friendship must be pending" }
    return PendingFriendship (
        uuid = this.id.value.toKotlinUuid(),
        direction = directionFor(userId),
        otherUserId = userId,
        createdAt = this.createdAt.toInstant(),
        updatedAt = this.updatedAt.toInstant()
    )
}

fun FriendshipEntity.toAcceptedDomain(userId: Uuid) : AcceptedFriendship {
    require(this.status == FriendshipStatus.ACCEPTED) { "Friendship must be ACCEPTED" }
    return AcceptedFriendship(
        uuid = this.id.value.toKotlinUuid(),
        otherUserId = userId,
        createdAt = this.createdAt.toInstant(),
        updatedAt = this.updatedAt.toInstant()
    )
}

fun FriendshipEntity.toDeletedDomain(userId: Uuid) : DeletedFriendship {
    require(this.status == FriendshipStatus.REMOVED) { "Friendship must be REMOVED" }
    require(this.deletedAt != null) { "DeletedFriendship must have an DeletedAt" }
    return DeletedFriendship(
        uuid = this.id.value.toKotlinUuid(),
        otherUserId = userId,
        createdAt = this.createdAt.toInstant(),
        updatedAt = this.updatedAt.toInstant(),
        deletedAt = this.deletedAt!!.toInstant()
    )
}

private fun FriendshipEntity.isRequestedBy(userId: Uuid) : Boolean = this.fromUserId.value.toKotlinUuid() == userId
private fun FriendshipEntity.directionFor(userId: Uuid) : FriendshipDirection = if (isRequestedBy(userId)) FriendshipDirection.OUTGOING else FriendshipDirection.INCOMING
private fun FriendshipEntity.friendEntity(userId: Uuid) : UserEntity = if (isRequestedBy(userId)) this.pairB else this.pairA