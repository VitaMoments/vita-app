package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.daily.StreakSummary
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.requests.user_requests.UpdateMyAccountRequest
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<UserWithContext>
    suspend fun searchUsers(currentUserId: Uuid, query: String?, limit: Int = 20, offset: Int = 0): RepositoryResult<PagedResult<User>>
    suspend fun getMyAccount(userId: Uuid): RepositoryResult<UserWithContext>
    suspend fun updateMyAccount(userId: Uuid, request: UpdateMyAccountRequest): RepositoryResult<AccountUser>
    suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser>
    suspend fun getCurrentStreakSummary(uuid: Uuid): RepositoryResult<StreakSummary>
}