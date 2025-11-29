@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class, InternalAPI::class)

package nl.fbdevelopment.healthyplatform.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.utils.io.InternalAPI
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import nl.fbdevelopment.healthyplatform.data.enums.AuthTokenType
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.nowPlusSeconds
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.toInstant
import nl.fbdevelopment.healthyplatform.data.models.domain.AuthToken
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import java.util.Date
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class JWTConfig(
    val issuer: String,
    val audience: String,
    val realm: String = "access",
    val secret: String,
    val jwtExpirationSeconds: Long = 3600,
    val refreshExpirationSeconds: Long = 604800
) {
    companion object {
        const val JWT_SUBJECT = "Authentication"
        const val USER_KEY = "userId"
    }

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(JWT_SUBJECT)
        .build()

    private fun generateToken(userId: Uuid, exp: Instant) : String = JWT.create()
            .withSubject(JWT_SUBJECT)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(USER_KEY, userId.toString())
            .withIssuedAt(Date(Clock.System.now().toEpochMilliseconds()))
            .withExpiresAt(Date(exp.toEpochMilliseconds()))
            .sign(algorithm)

    fun generateAccessToken(user: User): AuthToken {
        val exp = (Clock.System.now() + jwtExpirationSeconds.seconds)
        return AuthToken(
            token = generateToken(userId = user.uuid, exp = exp),
            expiredAt = exp,
            type = AuthTokenType.JWT
        )
    }

    fun generateRefreshToken(): AuthToken {
        val exp = (Clock.System.now() + refreshExpirationSeconds.seconds)
        return AuthToken(
            token = Uuid.random().toString(),
            expiredAt = exp,
            type = AuthTokenType.REFRESH
        )
    }
}
