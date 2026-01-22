package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.domain.feed.FeedItem
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.domain.richtext.RichTextRenderer
import eu.vitamoments.app.data.models.dto.feed.BlogItemDto
import eu.vitamoments.app.data.models.dto.feed.FeedItemDto
import eu.vitamoments.app.data.models.dto.feed.TimelineItemDto
import kotlin.time.Instant

fun FeedItem.toDto() = when(this) {
    is BlogItem -> this.toDto()
    is TimelineItem -> this.toDto()
}

fun FeedItemDto.toDomain() = when(this) {
    is BlogItemDto -> this.toDomain()
    is TimelineItemDto -> this.toDomain()
}

fun BlogItem.toDto(): BlogItemDto = BlogItemDto(
    uuid = this.uuid,
    author = this.author.toDto(),
    title = this.title,
    subtitle = this.subtitle,
    slug = this.slug,
    coverImageUrl = this.coverImageUrl,
    coverImageAlt = this.coverImageAlt,
    content = this.content,
    html = RichTextRenderer.toSafeHtml(this.content),
    categories = this.categories,
    privacy = this.privacy,
    status = this.status,
    publishedAt = this.publishedAt?.toEpochMilliseconds(),
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = this.updatedAt.toEpochMilliseconds(),
    deletedAt = this.deletedAt?.toEpochMilliseconds(),
)

fun BlogItemDto.toDomain() : BlogItem = BlogItem(
    uuid = this.uuid,
    author = this.author.toDomain(),
    title = this.title,
    subtitle = this.subtitle,
    slug = this.slug,
    coverImageUrl = this.coverImageUrl,
    coverImageAlt = this.coverImageAlt,
    content = this.content,
    categories = this.categories,
    privacy = this.privacy,
    status = this.status,
    publishedAt = this.publishedAt?.let{ Instant.fromEpochMilliseconds(it) },
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt),
    deletedAt = this.deletedAt?.let{ Instant.fromEpochMilliseconds(it) },
)

fun TimelineItem.toDto() : TimelineItemDto = TimelineItemDto(
    uuid = this.uuid,
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = this.updatedAt.toEpochMilliseconds(),
    deletedAt = this.deletedAt?.toEpochMilliseconds(),
    content = this.content,
    author = this.author.toDto(),
    plainText = RichTextRenderer.toPlainText(this.content),
    html = RichTextRenderer.toSafeHtml(this.content),
    privacy = this.privacy
)

fun TimelineItemDto.toDomain() : TimelineItem = TimelineItem(
    uuid = this.uuid,
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt),
    deletedAt = this.deletedAt?.let { Instant.fromEpochMilliseconds(it) },
    content = this.content,
    author = this.author.toDomain(),
    privacy = this.privacy
)