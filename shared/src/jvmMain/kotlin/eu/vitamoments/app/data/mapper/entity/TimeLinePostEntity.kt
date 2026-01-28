package eu.vitamoments.app.data.mapper.entity

import kotlinx.serialization.json.jsonObject
import eu.vitamoments.app.data.entities.TimeLineItemEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun TimeLineItemEntity.toDomain(
    viewerUuid: Uuid,
    friendshipStatusProvider: (authorUuid: Uuid) -> FriendshipStatus?
) : TimelineItem {
    val authorUuid = this.createdBy.id.value.toKotlinUuid()
    val status = friendshipStatusProvider(authorUuid)
    return TimelineItem(
    uuid = authorUuid,
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    author = this.createdBy.toDomainForViewer(viewerUuid, status),
    content = RichTextDocument(this.content.jsonObject)
)
}

fun TimeLineItemEntity.toDomain(viewerUuid: Uuid): TimelineItem =
    toDomain(viewerUuid) { null }