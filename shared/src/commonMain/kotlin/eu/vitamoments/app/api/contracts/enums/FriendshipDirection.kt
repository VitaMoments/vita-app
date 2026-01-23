package eu.vitamoments.app.api.contracts.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipDirection {
    INCOMING, OUTGOING, BOTH
}