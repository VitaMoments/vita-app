package nl.fbdevelopment.healthyplatform.data.mapper

import nl.fbdevelopment.healthyplatform.data.models.domain.AuthSession
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.AuthSessionDto

fun AuthSession.toAuthSessionDto() : AuthSessionDto = AuthSessionDto(
    user = this.user.toDto()
)