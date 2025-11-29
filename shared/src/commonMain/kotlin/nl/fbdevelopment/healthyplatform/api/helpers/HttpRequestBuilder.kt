package nl.fbdevelopment.healthyplatform.api.helpers

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import nl.fbdevelopment.healthyplatform.data.models.domain.AuthSession

fun HttpRequestBuilder.withAuthCookies(session: AuthSession) {
    val cookies = listOf(
        "access_token=${session.accessToken.token}",
        "refresh_token=${session.refreshToken.token}",
    ).joinToString(separator = "; ")
    header(HttpHeaders.Cookie, cookies)
}