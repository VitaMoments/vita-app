package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.enums.UserRole
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface User {
    val uuid: Uuid
    val displayName: String
    val bio: String?
    val imageUrl: String?
    val role: UserRole
}