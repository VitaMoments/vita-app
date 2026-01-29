package eu.vitamoments.app.api.helpers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import eu.vitamoments.app.config.JWTConfig
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.models.domain.AuthSession
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

suspend fun ApplicationCall.requireUserId() : Uuid = userId ?: run {
    respond(HttpStatusCode.Unauthorized, "UserId not found in requireUserId")
    error("Unauthorized call in requireUserId")
}

suspend fun ApplicationCall.requireRefreshToken() : String = refreshToken ?: run {
    respond(HttpStatusCode.Unauthorized, "RefreshToken not found in request")
    error("Unauthorized call in requireRefreshToken")
}

suspend fun ApplicationCall.requireAccessToken() : String = accessToken ?: run {
    respond(HttpStatusCode.Unauthorized, "AccessToken not found in request")
    error("Unauthorized call in requireAccessToken")
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
    val env = (System.getenv("ENVIRONMENT") ?: "").trim().lowercase()
    val isProdLike = env in setOf("prod", "production", "acc", "accept", "demo")

    response.cookies.append(
        Cookie(
            name = key,
            value = value,
            path = path,
            maxAge = maxAge?.toInt(),
            httpOnly = true,
            secure = isProdLike, // ✅ in prod https: true
            extensions = mapOf(
                // ✅ cross-site cookies: MUST be None + Secure
                "SameSite" to if (isProdLike) "None" else "Lax"
            )
        )
    )
}