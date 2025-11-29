package nl.fbdevelopment.healthyplatform.data.models.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationDto(
    val email: String,
    val password: String
)
