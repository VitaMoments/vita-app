package eu.vitamoments.app.data.models.domain

import eu.vitamoments.app.data.models.domain.user.User

data class AuthSession(
    val accessToken: AuthToken,
    val refreshToken: AuthToken,
    val user: User
)
