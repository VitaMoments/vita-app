package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.BlogItemEntity
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.dbHelpers.kotlinUuid
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun BlogItemEntity.toDomain(
    viewerUuid: Uuid,
    friendshipStatusProvider: (authorUuid: Uuid) -> FriendshipStatus?
) : BlogItem {
    val authorUuid = this.feedItem.author.id.value.toKotlinUuid()
    val status = friendshipStatusProvider(authorUuid)

    return BlogItem(
        this.kotlinUuid,
        title = this.title,
        subtitle = this.subtitle,
        slug = this.slug,
        coverImageAlt = this.coverImageAlt,
        coverImageUrl = this.coverImageUrl,
        content = RichTextDocument(content = this.content,),
        status = this.status,
        categories = this.feedItem.categories,
        createdAt = this.feedItem.createdAt.toInstant(),
        updatedAt = this.feedItem.updatedAt.toInstant(),
        deletedAt = this.feedItem.deletedAt?.toInstant(),
        publishedAt = this.publishedAt?.toInstant(),
        author = this.feedItem.author.toDomainForViewer(viewerUuid, status),
    )
}

fun BlogItemEntity.toDomain(viewerUuid: Uuid): BlogItem = toDomain(viewerUuid) { null }