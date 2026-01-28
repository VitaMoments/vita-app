package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatus {
    PENDING, ACCEPTED, DECLINED, REMOVED;

    override fun toString(): String = name

    companion object {
        val activeEntries: Set<FriendshipStatus> = setOf<FriendshipStatus>(PENDING, ACCEPTED)
    }
}