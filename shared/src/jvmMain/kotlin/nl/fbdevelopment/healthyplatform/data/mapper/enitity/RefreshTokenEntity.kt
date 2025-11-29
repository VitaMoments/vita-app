@file:OptIn(ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.mapper.enitity

import nl.fbdevelopment.healthyplatform.data.entities.RefreshTokenEntity
import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.enums.AuthTokenType
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.toInstant
import nl.fbdevelopment.healthyplatform.data.models.domain.AuthToken
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.mappers.toUtcLocalDateTime
import kotlin.time.ExperimentalTime

fun RefreshTokenEntity.toDomain() : AuthToken = AuthToken(
    token = this.refreshToken,
    expiredAt = this.expiredAt.toInstant(),
    type = AuthTokenType.REFRESH,
    revoked = this.revokedAt
)

fun RefreshTokenEntity.Companion.fromAuthToken(
    authToken: AuthToken,
    user: UserEntity
): RefreshTokenEntity = new {
    this.refreshToken = authToken.token
    this.expiredAt = authToken.expiredAt.toUtcLocalDateTime()
    this.user = user
}