package eu.vitamoments.app.api.contracts.enums


enum class TimeLineFeed {
    SELF,
    FRIENDS,
    DISCOVERY,
    GROUPS;

    override fun toString(): String = this.name
}