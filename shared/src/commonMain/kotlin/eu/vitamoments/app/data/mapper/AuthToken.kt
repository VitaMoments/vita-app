@file:OptIn(ExperimentalTime::class)

package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.AuthToken
import eu.vitamoments.app.data.models.dto.auth.AuthTokenDto
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