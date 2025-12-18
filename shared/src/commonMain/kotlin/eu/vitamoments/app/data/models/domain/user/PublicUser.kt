@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.domain.user

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class PublicUser(
    val uuid: Uuid,
    val email: String,
    val imageUrl: String? = null
)
