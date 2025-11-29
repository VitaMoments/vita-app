@file:OptIn(ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.mapper

import nl.fbdevelopment.healthyplatform.data.models.domain.AuthToken
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.AuthTokenDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun AuthToken.toAuthTokenDto() : AuthTokenDto = AuthTokenDto(
    token = this.token,
    expiredAt = this.expiredAt.toEpochMilliseconds(),
    type = this.type
)

fun AuthTokenDto.toAuthToken() : AuthToken = AuthToken(
    token = this.token,
    expiredAt = Instant.fromEpochMilliseconds(this.expiredAt),
    type = this.type
)