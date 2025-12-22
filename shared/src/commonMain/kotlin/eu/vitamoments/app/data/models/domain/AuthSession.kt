package eu.vitamoments.app.data.models.domain

import eu.vitamoments.app.data.models.domain.user.AccountUser

data class AuthSession(
    val accessToken: AuthToken,
    val refreshToken: AuthToken,
    val user: AccountUser
)
