package eu.vitamoments.app.data.models.dto.user

import eu.vitamoments.app.data.enums.FriendshipDirection
import eu.vitamoments.app.data.enums.FriendshipStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface FriendshipDto{
    val uuid: Uuid
    val status: FriendshipStatus
    val createdAt: Long
    val updatedAt: Long
}

@Serializable
@SerialName("PENDING")
data class PendingFriendshipDto(
    override val uuid: Uuid,
    override val createdAt: Long,
    override val updatedAt: Long,
    val direction: FriendshipDirection,
    val friend: PublicUserDto,
) : FriendshipDto {
    override val status: FriendshipStatus = FriendshipStatus.PENDING
}

@Serializable
@SerialName("ACCEPTED")
data class AcceptedFriendshipDto(
    override val uuid: Uuid,
    override val createdAt: Long,
    override val updatedAt: Long,
    val friend: PrivateUserDto,
) : FriendshipDto {
    override val status: FriendshipStatus = FriendshipStatus.ACCEPTED
}


