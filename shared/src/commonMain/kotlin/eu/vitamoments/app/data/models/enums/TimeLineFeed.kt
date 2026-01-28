package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class TimeLineFeed {
    SELF,
    FRIENDS,
    DISCOVERY,
    GROUPS;

    override fun toString(): String = this.name
}