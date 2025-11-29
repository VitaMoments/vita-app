@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.mapper.enitity

import kotlinx.serialization.json.jsonObject
import nl.fbdevelopment.healthyplatform.data.entities.TimeLinePostEntity
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.toInstant
import nl.fbdevelopment.healthyplatform.data.models.domain.message.TimeLinePost
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