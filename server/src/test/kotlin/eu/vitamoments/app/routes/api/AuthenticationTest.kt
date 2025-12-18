package eu.vitamoments.app.routes.api

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import eu.vitamoments.app.api.helpers.withAuthCookies
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.models.user.TestUsers
import eu.vitamoments.app.utils.DbIntegrationTest
import eu.vitamoments.app.utils.setupApp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class AuthenticationTest : DbIntegrationTest() {

    @Test
    fun `login should set access and refresh cookies`() = testApplication {
        setupApp()

        val user = TestUsers.default()

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": ${user.email}, "password": "password"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie)
        assertNotNull(setCookieHeaders, "Expected Set-Cookie headers from login")

        val accessCookieHeader = setCookieHeaders?.find { it.startsWith("access_token=") }
        val refreshCookieHeader = setCookieHeaders?.find { it.startsWith("refresh_token=") }

        assertNotNull(accessCookieHeader, "access_token cookie must be set")
        assertNotNull(refreshCookieHeader, "refresh_token cookie must be set")

        assertTrue(accessCookieHeader!!.contains("HttpOnly"), "access_token should be HttpOnly")
        assertTrue(refreshCookieHeader!!.contains("HttpOnly"), "refresh_token should be HttpOnly")
    }

    @Test
    fun `login with wrong email should give Unauthorized`() = testApplication {
        setupApp()
        val user = TestUsers.default()
        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "test@example.com", "password": "password"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status, "Expected should be Not Found, actual is ${response.status}")
    }

    @Test
    fun `login with wrong password should give Unauthorized`() = testApplication {
        setupApp()
        val user = TestUsers.default()
        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": ${user.email}, "password": "fake_password"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status, "Expected should be Not Found, actual is ${response.status}")
    }

    @Test
    fun `register should create a new user and sets access and refresh cookies`() = testApplication {
        setupApp()

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "some_email@example.com", "password": "password"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status, "Expected should be Ok, actual is ${response.status}")

        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie)
        assertNotNull(setCookieHeaders, "Expected Set-Cookie headers from register")

        val accessCookieHeader = setCookieHeaders?.find { it.startsWith("access_token=") }
        val refreshCookieHeader = setCookieHeaders?.find { it.startsWith("refresh_token=") }

        assertNotNull(accessCookieHeader, "access_token cookie must be set")
        assertNotNull(refreshCookieHeader, "refresh_token cookie must be set")

        assertTrue(accessCookieHeader!!.contains("HttpOnly"), "access_token should be HttpOnly")
        assertTrue(refreshCookieHeader!!.contains("HttpOnly"), "refresh_token should be HttpOnly")
    }

    @Test
    fun `refresh should create a new authSession`() = testApplication {
        setupApp()
        val user = TestUsers.default()
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "${user.email}", "password": "password"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val setCookieHeaders = loginResponse.headers.getAll(HttpHeaders.SetCookie)!!
        val cookieHeaderValue = setCookieHeaders.joinToString("; ") { it.substringBefore(";") }

        val refreshResponse = client.post("/api/auth/refresh") {
            header(HttpHeaders.Cookie, cookieHeaderValue)
        }
        assertEquals(HttpStatusCode.Created, refreshResponse.status)
    }

    @Test
    fun `refresh should revoke older token`() = testApplication {
        setupApp()
        val session: AuthSession = TestUsers.defaultAsAuthSession()

        val response = client.post("/api/auth/refresh") {
            withAuthCookies(session)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val newResponse = client.post("/api/auth/refresh") {
            withAuthCookies(session)
        }
        assertEquals(HttpStatusCode.Unauthorized, newResponse.status, "oldRefreshToken should not be valid")
    }
}
