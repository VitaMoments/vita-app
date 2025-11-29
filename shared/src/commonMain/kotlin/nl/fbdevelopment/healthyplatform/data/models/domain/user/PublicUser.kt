@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.models.domain.user

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class PublicUser(
    val uuid: Uuid,
    val email: String,
    val imageUrl: String? = null
)
