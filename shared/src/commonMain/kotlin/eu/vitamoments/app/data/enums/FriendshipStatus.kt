package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatus {
    PENDING, ACCEPTED, DECLINED, REMOVED;

    override fun toString(): String = name

    val isFriend: Boolean
        get() = this == ACCEPTED

    companion object {
        val activeEntries: Set<FriendshipStatus> = setOf<FriendshipStatus>(PENDING, ACCEPTED)
    }
}

fun FriendshipStatus.isActive(): Boolean =
    this == FriendshipStatus.PENDING || this == FriendshipStatus.ACCEPTED