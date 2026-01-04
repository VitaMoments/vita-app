package eu.vitamoments.app.data.models.dto.user

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoveFriendshipDto(
    val friendId: Uuid
)
