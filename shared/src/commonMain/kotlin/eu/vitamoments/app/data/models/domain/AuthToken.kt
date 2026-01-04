package eu.vitamoments.app.data.models.domain

import kotlinx.datetime.LocalDateTime
import eu.vitamoments.app.data.enums.AuthTokenType
import kotlin.time.Clock
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
