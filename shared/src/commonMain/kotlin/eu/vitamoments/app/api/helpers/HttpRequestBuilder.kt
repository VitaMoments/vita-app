package eu.vitamoments.app.api.helpers

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import eu.vitamoments.app.data.models.domain.AuthSession

fun HttpRequestBuilder.withAuthCookies(session: AuthSession) {
    val cookies = listOf(
        "access_token=${session.accessToken.token}",
        "refresh_token=${session.refreshToken.token}",
    ).joinToString(separator = "; ")
    header(HttpHeaders.Cookie, cookies)
}