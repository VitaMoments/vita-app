package nl.fbdevelopment.healthyplatform.routes.api

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import nl.fbdevelopment.healthyplatform.data.models.dto.message.CreateTimeLinePostDto
import nl.fbdevelopment.healthyplatform.models.posts.TestTimeLinePosts
import nl.fbdevelopment.healthyplatform.models.user.TestUsers
import nl.fbdevelopment.healthyplatform.utils.DbIntegrationTest
import nl.fbdevelopment.healthyplatform.utils.setupApp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimeLineTest: DbIntegrationTest() {
    @Test
    fun `post should create new Post`() = testApplication {
        setupApp()

        val user = TestUsers.default()
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "${user.email}", "password": "password"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val setCookieHeaders = loginResponse.headers.getAll(HttpHeaders.SetCookie)!!
        val cookieHeaderValue = setCookieHeaders.joinToString("; ") { it.substringBefore(";") }

        val dto = TestTimeLinePosts.generateCreateTimelineDto()
        val bodyJson = Json.encodeToString(CreateTimeLinePostDto.serializer(), dto)

        val response = client.post("/api/timeline") {
            header(HttpHeaders.Cookie, cookieHeaderValue)
            contentType(ContentType.Application.Json)
            setBody(bodyJson)
        }
        println("STATUS = ${response.status}")
        println("BODY   = ${response.bodyAsText()}")
        assertEquals(HttpStatusCode.Created, response.status)
    }
}