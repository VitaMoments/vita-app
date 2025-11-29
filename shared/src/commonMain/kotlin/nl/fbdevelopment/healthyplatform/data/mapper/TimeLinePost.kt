@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.mapper

import nl.fbdevelopment.healthyplatform.data.models.domain.message.TimeLinePost
import nl.fbdevelopment.healthyplatform.data.models.dto.message.TimeLinePostDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

fun TimeLinePost.toDto() : TimeLinePostDto = TimeLinePostDto(
    uuid = this.uuid,
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = this.updatedAt.toEpochMilliseconds(),
    deletedAt = this.deletedAt?.toEpochMilliseconds(),
    content = this.content,
    userDto = this.createdBy.toDto(),
    plainText = this.plainText,
    html = this.html
)

fun TimeLinePostDto.toDomain() : TimeLinePost = TimeLinePost(
    uuid = this.uuid,
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt),
    deletedAt = this.deletedAt?.let { Instant.fromEpochMilliseconds(it) },
    createdBy = this.userDto.toDomain(),
    content = this.content
)