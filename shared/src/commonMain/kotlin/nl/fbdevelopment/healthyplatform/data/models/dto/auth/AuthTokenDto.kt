package nl.fbdevelopment.healthyplatform.data.models.dto.auth

import kotlinx.serialization.Serializable
import nl.fbdevelopment.healthyplatform.data.enums.AuthTokenType

@Serializable
data class AuthTokenDto(
    val token: String,
    val expiredAt: Long,
    val type: AuthTokenType
)
