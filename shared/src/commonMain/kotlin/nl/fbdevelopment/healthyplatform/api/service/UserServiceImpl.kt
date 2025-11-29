package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import nl.fbdevelopment.healthyplatform.data.models.dto.user.UserDto

class UserServiceImpl(private val client: HttpClient) : UserService {
    override suspend fun updateUser(body: UserDto): HttpResponse = client.post("/users") {
        setBody(body)
    }

}