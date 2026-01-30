package eu.vitamoments.app.api.helpers

import eu.vitamoments.app.config.BuildConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import eu.vitamoments.app.config.JWTConfig
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.domain.api.ErrorCode
import eu.vitamoments.app.data.models.requests.respondError
import eu.vitamoments.app.data.repository.RepositoryError
import kotlin.uuid.Uuid
import io.ktor.http.Cookie

val ApplicationCall.userId: Uuid?
    get() = principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(JWTConfig.USER_KEY)
        ?.asString()
        ?.let {
            runCatching {  Uuid.parse(it) }.getOrNull()
        }

val ApplicationCall.refreshToken: String?
    get() = this.request.cookies["refresh_token"]

val ApplicationCall.accessToken: String?
    get() = this.request.cookies["access_token"]

suspend fun ApplicationCall.requireUserId() : Uuid = userId ?: unauthorized(
    ErrorCode.ACCESS_TOKEN_NOT_VALID,
    "Invalid AccessToken"
)

suspend fun ApplicationCall.requireRefreshToken(): String =
    refreshToken ?: unauthorized(
        ErrorCode.REFRESH_TOKEN_NOT_FOUND,
        "RefreshToken not found")


suspend fun ApplicationCall.requireAccessToken(): String =
    accessToken ?: unauthorized(ErrorCode.ACCESS_TOKEN_NOT_FOUND, "AccessToken not found")




suspend fun ApplicationCall.unauthorized(code: ErrorCode, message: String): Nothing {
    respondError(RepositoryError.Unauthorized(message = message, code = code))
    error("Unauthorized: $code - $message")
}


fun ApplicationCall.clearAuthCookies() {
    appendCookie("access_token", "", maxAge = 0, path = "/api")
    appendCookie("refresh_token", "", maxAge = 0, path = "/api/auth")
}

fun ApplicationCall.setAuthCookies(session: AuthSession) {
    val jwtConfig = JWTConfigLoader.loadOrThrow()
    appendCookie("access_token", session.accessToken.token, jwtConfig.jwtExpirationSeconds, path = "/api")
    appendCookie("refresh_token", session.refreshToken.token, jwtConfig.refreshExpirationSeconds, path = "/api/auth")
}

private fun ApplicationCall.appendCookie(
    key: String,
    value: String,
    maxAge: Long? = null,
    path: String
) {
    val env = BuildConfig.ENVIRONMENT
    val isProdLike = env in setOf("prod", "production", "acc", "accept", "demo")

    response.cookies.append(
        Cookie(
            name = key,
            value = value,
            path = path,
            maxAge = maxAge?.toInt(),
            httpOnly = true,
            secure = isProdLike,
            extensions = mapOf(
                "SameSite" to if (isProdLike) "None" else "Lax"
            )
        )
    )
}