@file:OptIn(ExperimentalTime::class)

package eu.vitamoments.app.data.mapper.enitity

import eu.vitamoments.app.data.entities.RefreshTokenEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.enums.AuthTokenType
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.AuthToken
import eu.vitamoments.app.mappers.toUtcLocalDateTime
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