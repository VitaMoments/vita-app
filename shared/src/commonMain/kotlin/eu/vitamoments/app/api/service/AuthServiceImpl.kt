package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class AuthServiceImpl(private val client: HttpClient) : AuthService {
    override suspend fun login(body: LoginRequest): HttpResponse = client.post("/auth/register") {
        setBody(body)
    }
    override suspend fun logout() : HttpResponse = client.post("/auth/logout")
    override suspend fun register(body: RegistrationRequest): HttpResponse = client.post("/auth/register") {
        setBody(body)
    }
    override suspend fun refreshTokens(): HttpResponse = client.post("/auth/refresh_tokens")
    override suspend fun session(): HttpResponse = client.get("/auth/session")
}