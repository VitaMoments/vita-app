package nl.fbdevelopment.healthyplatform.data.models.dto.auth

import kotlinx.serialization.Serializable
import nl.fbdevelopment.healthyplatform.data.models.dto.user.UserDto

@Serializable
data class AuthSessionDto(
    val user: UserDto
)
