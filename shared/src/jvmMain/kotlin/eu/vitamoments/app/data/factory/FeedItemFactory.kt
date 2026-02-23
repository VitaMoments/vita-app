package eu.vitamoments.app.data.factory

import eu.vitamoments.app.data.entities.BlogItemEntity
import eu.vitamoments.app.data.entities.FeedItemEntity
import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

object FeedItemFactory {

    fun newBlogItem(
        author: UserEntity,
        title: String,
        content: JsonElement,
        subtitle: String? = null,
        coverImageUrl: String? = null,
        coverImageAlt: String? = null,
        categories: List<FeedCategory> = emptyList(),
        status: BlogStatus = BlogStatus.DRAFT,
        privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
        publishedAt: LocalDateTime? = null
    ): BlogItemEntity {
        val feedItemEntity = newFeedItemEntity(
            type = FeedItemType.BLOG,
            author = author,
            privacy = privacy,
            categories = categories
        )

        return BlogItemEntity.new(feedItemEntity.id.value) {
            this.feedItemId = feedItemEntity.id
            this.title = title
            this.subtitle = subtitle
            this.slug = makeSlug(title, feedItemEntity.id.value.toKotlinUuid())
            this.coverImageUrl = coverImageUrl
            this.coverImageAlt = coverImageAlt
            this.status = status
            this.publishedAt = publishedAt
            this.content = content
        }
    }

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

    private fun slugify(s: String): String =
        s.lowercase()
            .trim()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifBlank { "post" }

    private fun makeSlug(title: String, id: Uuid): String {
        val base = slugify(title)
        return "$base-${id.toString().take(8)}"
    }
}