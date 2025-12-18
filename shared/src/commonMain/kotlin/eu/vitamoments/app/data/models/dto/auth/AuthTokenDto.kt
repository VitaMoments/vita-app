package eu.vitamoments.app.data.models.dto.auth

import kotlinx.serialization.Serializable
import eu.vitamoments.app.data.enums.AuthTokenType

@Serializable
data class AuthTokenDto(
    val token: String,
    val expiredAt: Long,
    val type: AuthTokenType
)
