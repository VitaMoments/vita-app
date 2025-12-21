package eu.vitamoments.app.data.models.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationDto(
    val username: String,
    val email: String,
    val password: String
)
