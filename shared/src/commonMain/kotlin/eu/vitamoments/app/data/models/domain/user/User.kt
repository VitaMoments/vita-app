@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.domain.user

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val uuid: Uuid,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
    var imageUrl: String? = null
)