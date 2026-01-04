package eu.vitamoments.app.data.models.domain.user

import kotlin.uuid.Uuid

sealed interface User {
    val uuid: Uuid
    val alias: String?
    val bio: String?
    val imageUrl: String?
}