package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipDirection {
    INCOMING, OUTGOING, BOTH
}