@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.data.mapper.toDomain
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.dto.user.AccountUserDto
import eu.vitamoments.app.data.models.dto.user.UserDto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResponse<User> {
        val response = service.getUser(userId)
        return response.toRepositoryResponse<UserDto, User>{ dto -> dto.toDomain() }
    }

    override suspend fun searchUsers(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<List<User>> {
        val response = service.searchUsers(query, limit, offset)
        return response.toRepositoryResponse<List<UserDto>, List<User>> { listDto -> listDto.map { dto-> dto.toDomain() } }
    }

    override suspend fun getMyAccount(userId: Uuid): RepositoryResponse<AccountUser> {
        val response = service.getMyAccount()
        return response.toRepositoryResponse<AccountUserDto, AccountUser> { dto -> dto.toDomain() }
    }

    override suspend fun updateMyAccount(): RepositoryResponse<AccountUser> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResponse<AccountUser> {
        TODO("Not yet implemented")
    }

}