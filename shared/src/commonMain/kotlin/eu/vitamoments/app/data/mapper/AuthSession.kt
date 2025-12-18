package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.dto.auth.AuthSessionDto

fun AuthSession.toAuthSessionDto() : AuthSessionDto = AuthSessionDto(
    user = this.user.toDto()
)