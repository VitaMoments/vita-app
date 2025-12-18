package eu.vitamoments.app.api.service

import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.dto.user.UserDto

interface UserService {
    suspend fun updateUser(body: UserDto) : HttpResponse
}