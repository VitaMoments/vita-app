package eu.vitamoments.app.data.factory

import eu.vitamoments.app.data.entities.FeedItemEntity
import eu.vitamoments.app.data.entities.DailyQuestionEntity
import eu.vitamoments.app.data.entities.DailyQuestionItemEntity
import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.datetime.LocalDate
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

    fun newDailyQuestionItem(
        author: UserEntity,
        question: DailyQuestionEntity,
        questionDate: LocalDate,
        privacy: PrivacyStatus = PrivacyStatus.PRIVATE
    ): DailyQuestionItemEntity {
        val feedItemEntity = newFeedItemEntity(
            type = FeedItemType.DAILY_QUESTION,
            author = author,
            privacy = privacy
        )

        return DailyQuestionItemEntity.new(feedItemEntity.id.value) {
            this.feedItem = feedItemEntity
            this.question = question
            this.questionDate = questionDate
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