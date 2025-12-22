@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.domain.user.PublicUser
import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.dto.user.PrivateUserDto
import eu.vitamoments.app.data.repository.RepositoryResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserService {
    suspend fun getUser(userId: Uuid) : HttpResponse
    suspend fun searchUsers(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun getMyAccount(): HttpResponse
    suspend fun updateMyAccount(): HttpResponse
    suspend fun updateMyProfileImage(): HttpResponse
}

/*
    suspend fun getPublicUser(userId: Uuid): RepositoryResponse<PublicUser>
    suspend fun searchPublicUsers(query: String?, limit: Int = 20, offset: Int = 0) : RepositoryResponse<List<PublicUser>>

    suspend fun getPrivateUser(userId: Uuid): RepositoryResponse<PrivateUser>
    suspend fun getPrivateUsers(query: String?, limit: Int = 20, offset: Int = 0)

    suspend fun getMyAccount(): RepositoryResponse<AccountUser>
    suspend fun updateMyAccount(): RepositoryResponse<AccountUser>
    suspend fun updateMyProfileImage(url: String): RepositoryResponse<AccountUser>
 */