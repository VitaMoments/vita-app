package eu.vitamoments.app.data.mapper.entity

import kotlinx.serialization.json.jsonObject
import eu.vitamoments.app.data.entities.TimeLinePostEntity
import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun TimeLinePostEntity.toDomain(
    viewerUuid: Uuid,
    friendshipStatusProvider: (authorUuid: Uuid) -> FriendshipStatus?
) : TimeLinePost {
    val authorUuid = this.createdBy.id.value.toKotlinUuid()
    val status = friendshipStatusProvider(authorUuid)
    return TimeLinePost(
    uuid = authorUuid,
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    createdBy = this.createdBy.toDomainForVieuwer(viewerUuid, status),
    content = this.content.jsonObject
)
}

fun TimeLinePostEntity.toDomain(viewerUuid: Uuid): TimeLinePost =
    toDomain(viewerUuid) { null }