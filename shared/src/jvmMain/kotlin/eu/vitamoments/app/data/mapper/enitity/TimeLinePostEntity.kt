@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.mapper.enitity

import kotlinx.serialization.json.jsonObject
import eu.vitamoments.app.data.entities.TimeLinePostEntity
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

fun TimeLinePostEntity.toDomain() : TimeLinePost = TimeLinePost(
    uuid = this.id.value.toKotlinUuid(),
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    createdBy = this.createdBy.toPublicDomain(),
    content = this.content.jsonObject
)