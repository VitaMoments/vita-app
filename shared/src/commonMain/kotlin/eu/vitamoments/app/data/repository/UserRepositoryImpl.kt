package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.data.mapper.toRepositoryResult
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import io.ktor.client.call.body
import kotlin.uuid.Uuid

class UserRepositoryImpl(private val service: UserService) : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<User> =
        service.getUser(userId = userId).toRepositoryResult()

    override suspend fun getMyAccount(userId: Uuid): RepositoryResult<AccountUser>  =
        service.getMyAccount().toRepositoryResult()

    override suspend fun updateMyAccount(): RepositoryResult<AccountUser> =
        service.updateMyAccount().toRepositoryResult()

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser> =
        service.updateMyProfileImage().body()

}