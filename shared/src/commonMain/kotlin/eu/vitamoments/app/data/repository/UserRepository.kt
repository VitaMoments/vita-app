@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResponse<User>
    suspend fun searchUsers(userId: Uuid, query: String?, limit: Int = 20, offset: Int = 0) : RepositoryResponse<List<User>>

    suspend fun getMyAccount(userId: Uuid): RepositoryResponse<AccountUser>
    suspend fun updateMyAccount(): RepositoryResponse<AccountUser>
    suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResponse<AccountUser>
}