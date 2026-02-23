package eu.vitamoments.app.data.factory

import eu.vitamoments.app.data.entities.BlogItemEntity
import eu.vitamoments.app.data.entities.FeedItemEntity
import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.dbHelpers.dbQuery
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlin.uuid.toKotlinUuid

object FeedItemFactory {
    suspend fun newBlogItem(
        author: UserEntity,
        title: String,
        slug: String,
        content: JsonElement,
        subtitle: String? = null,
        coverImageUrl: String? = null,
        coverImageAlt: String? = null,
        status: BlogStatus = BlogStatus.DRAFT,
        privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
        publishedAt: LocalDateTime? = null
    ): BlogItemEntity = dbQuery {
        val feedItemEntity = newFeedItemEntity(
            type = FeedItemType.BLOG,
            author = author,
            privacy = privacy
        )

        BlogItemEntity.new(feedItemEntity.id.value) {
            this.feedItemId = feedItemEntity.id
            this.title = title
            this.subtitle = subtitle
            this.slug = slug
            this.coverImageUrl = coverImageUrl
            this.coverImageAlt = coverImageAlt
            this.status = status
            this.publishedAt = publishedAt
            this.content = content
        }
    }

    suspend fun newTimelineItem(
        author: UserEntity,
        content: JsonElement,
        privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY
    ): TimelineItemEntity = dbQuery {
        val feedItemEntity = newFeedItemEntity(
            type = FeedItemType.BLOG,
            author = author,
            privacy = privacy
        )

        TimelineItemEntity.new(feedItemEntity.id.value) {
            this.feedItem = feedItemEntity
            this.content = content
        }
    }

    private suspend fun newFeedItemEntity(
        type: FeedItemType,
        author: UserEntity,
        privacy: PrivacyStatus
    ) : FeedItemEntity = dbQuery {
        FeedItemEntity.new {
            this.type = type
            this.author = author
            this.privacy = privacy
        }
    }
}