@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.dto.user

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class PublicUserDto(
    val uuid: Uuid,
    val email: String,
    val imageUrl: String? = null
)
