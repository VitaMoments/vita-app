@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.repository

import nl.fbdevelopment.healthyplatform.api.service.UserService
import nl.fbdevelopment.healthyplatform.data.mapper.toDomain
import nl.fbdevelopment.healthyplatform.data.mapper.toDto
import nl.fbdevelopment.healthyplatform.data.mapper.toRepositoryResponse
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.data.models.dto.user.UserDto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUserById(uuid: Uuid): RepositoryResponse<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): RepositoryResponse<User> {
        val response = service.updateUser(body = user.toDto())
        return response.toRepositoryResponse<UserDto, User> { dto -> dto.toDomain() }
    }

}