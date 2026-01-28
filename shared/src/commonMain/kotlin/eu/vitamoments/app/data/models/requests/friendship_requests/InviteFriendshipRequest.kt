package eu.vitamoments.app.data.models.requests.friendship_requests

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class InviteFriendshipRequest(
    val userId: Uuid
)
