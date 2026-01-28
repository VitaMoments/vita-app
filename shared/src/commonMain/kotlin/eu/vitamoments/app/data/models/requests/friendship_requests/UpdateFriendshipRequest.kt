package eu.vitamoments.app.data.models.requests.friendship_requests

import eu.vitamoments.app.data.models.domain.user.Friendship
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFriendshipRequest(
    val friendship: Friendship
)
