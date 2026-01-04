package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendInviteEventType {
    SENT,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    AUTO_REJECTED
}