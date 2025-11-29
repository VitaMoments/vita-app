@file:OptIn(ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.models.domain

import kotlinx.datetime.LocalDateTime
import nl.fbdevelopment.healthyplatform.data.enums.AuthTokenType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class AuthToken(
    val token: String,
    val expiredAt: Instant,
    val type: AuthTokenType,
    val revoked: LocalDateTime? = null
) {
    val duration : Long
        get() = (expiredAt - Clock.System.now()).inWholeSeconds

    val isExpired: Boolean
        get() = expiredAt <= Clock.System.now()

}
