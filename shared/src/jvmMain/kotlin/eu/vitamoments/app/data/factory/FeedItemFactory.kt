package eu.vitamoments.app.data.factory

import eu.vitamoments.app.data.entities.FeedItemEntity
import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.json.JsonElement

object FeedItemFactory {


    fun newTimelineItem(
        author: UserEntity,
        content: JsonElement,
        privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY
    ): TimelineItemEntity {
        val feedItemEntity = newFeedItemEntity(
            type = FeedItemType.TIMELINE,
            author = author,
            privacy = privacy
        )

        return TimelineItemEntity.new(feedItemEntity.id.value) {
            this.feedItem = feedItemEntity
            this.content = content
        }
    }

    private fun newFeedItemEntity(
        type: FeedItemType,
        author: UserEntity,
        privacy: PrivacyStatus,
        categories: List<FeedCategory> = emptyList()
    ): FeedItemEntity {
        val item = FeedItemEntity.new {
            this.type = type
            this.author = author
            this.privacy = privacy
        }

        item.refresh(true)
        item.categories = categories
        return item
    }
}