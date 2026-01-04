package eu.vitamoments.app.data.enums


enum class TimeLineFeed {
    SELF,
    FRIENDS,
    DISCOVERY,
    GROUPS;

    override fun toString(): String = this.name
}