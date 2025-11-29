package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.statement.HttpResponse
import nl.fbdevelopment.healthyplatform.data.models.dto.user.UserDto

interface UserService {
    suspend fun updateUser(body: UserDto) : HttpResponse
}