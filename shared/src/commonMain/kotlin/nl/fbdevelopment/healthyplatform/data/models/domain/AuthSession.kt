package nl.fbdevelopment.healthyplatform.data.models.domain

import nl.fbdevelopment.healthyplatform.data.models.domain.user.User

data class AuthSession(
    val accessToken: AuthToken,
    val refreshToken: AuthToken,
    val user: User
)
