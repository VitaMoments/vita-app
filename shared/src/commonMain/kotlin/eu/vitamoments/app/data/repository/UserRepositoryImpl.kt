package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.data.mapper.toRepositoryResult
import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.requests.user_requests.UpdateMyAccountRequest
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<UserWithContext> =
        service.getUser(userId = userId).toRepositoryResult()

    override suspend fun searchUsers(
        currentUserId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResult<PagedResult<User>> =
        service.searchUsers(query = query, limit = limit, offset = offset).toRepositoryResult()

    override suspend fun getMyAccount(userId: Uuid): RepositoryResult<AccountUser>  =
        service.getMyAccount().toRepositoryResult()

    override suspend fun updateMyAccount(
        userId: Uuid,
        request: UpdateMyAccountRequest
    ): RepositoryResult<AccountUser> =
        service.updateMyAccount(request).toRepositoryResult()

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser> {
        throw UnsupportedOperationException("Use media upload flow to update profile image")
    }

}