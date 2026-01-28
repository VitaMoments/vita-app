package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResponse<User> =
        service.getUser(userId = userId).toRepositoryResponse<User>()

    override suspend fun getMyAccount(userId: Uuid): RepositoryResponse<AccountUser>  =
        service.getMyAccount().toRepositoryResponse<AccountUser>()

    override suspend fun updateMyAccount(): RepositoryResponse<AccountUser> =
        service.updateMyAccount().toRepositoryResponse<AccountUser>()

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResponse<AccountUser> =
        service.updateMyProfileImage().toRepositoryResponse()

}