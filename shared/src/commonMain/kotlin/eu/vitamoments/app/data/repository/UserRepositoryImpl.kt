@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.data.mapper.toDomain
import eu.vitamoments.app.data.mapper.toDto
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.dto.user.UserDto
import eu.vitamoments.app.data.models.validation.validate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUserById(uuid: Uuid): RepositoryResponse<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): RepositoryResponse<User> {
        val errors = user.validate()
        require(errors.isEmpty()) {
            errors.joinToString(separator = "; ") { "${it.key}: ${it.message}" }
        }

        val response = service.updateUser(body = user.toDto())
        return response.toRepositoryResponse<UserDto, User> { dto -> dto.toDomain() }
    }

    override suspend fun updateImageUrl(
        userId: Uuid,
        url: String
    ): RepositoryResponse<User> {
        TODO("Don't use this on the server side.")
    }

}