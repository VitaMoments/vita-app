package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatus {
    PENDING, ACCEPTED, DECLINED, REMOVED
}