package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.dbHelpers.kotinUuid
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun TimelineItemEntity.toDomain(
    viewerUuid: Uuid,
    friendshipStatusProvider: (authorUuid: Uuid) -> FriendshipStatus?
) : TimelineItem {
    val authorUuid = this.feedItem.author.id.value.toKotlinUuid()
    val status = friendshipStatusProvider(authorUuid)
    return TimelineItem(
    uuid = this.kotinUuid,
    createdAt = this.feedItem.createdAt.toInstant(),
    updatedAt = this.feedItem.updatedAt.toInstant(),
    deletedAt = this.feedItem.deletedAt?.toInstant(),
    author = this.feedItem.author.toDomainForViewer(viewerUuid, status),
    content = RichTextDocument(content = this.content),
)
}

fun TimelineItemEntity.toDomain(viewerUuid: Uuid): TimelineItem = toDomain(viewerUuid) { null }