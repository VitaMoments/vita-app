package eu.vitamoments.app.data.models.dto.auth

import kotlinx.serialization.Serializable
import eu.vitamoments.app.data.models.dto.user.UserDto

@Serializable
data class AuthSessionDto(
    val user: UserDto
)
