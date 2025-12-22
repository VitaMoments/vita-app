@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.enitity.toAccountDomain
import eu.vitamoments.app.data.mapper.enitity.toPrivateDomain
import eu.vitamoments.app.data.mapper.enitity.toPublicDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.tables.FriendshipsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.ilike
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.exists
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class JVMUserRepository() : UserRepository {


    //    override suspend fun getUserById(uuid: Uuid): RepositoryResponse<User> = dbQuery {
//        val entity = UserEntity.findById(uuid.toJavaUuid())
//
//        if (entity == null) {
//            RepositoryResponse.Error.NotFound(
//                message = "User with id $uuid not found"
//            )
//        } else {
//            RepositoryResponse.Success(
//                body = entity.toDomain()
//            )
//        }
//    }
//
//    override suspend fun updateUser(user: User): RepositoryResponse<User> = dbQuery {
//        val entity = UserEntity.findByIdAndUpdate(id = user.uuid.toJavaUuid()) {
//            it.updatedAt = LocalDateTime.nowUtc()
//            it.imageUrl = user.imageUrl
//        }
//
//        if (entity == null) {
//            RepositoryResponse.Error.NotFound(
//                message = "User with id ${user.uuid} not found"
//            )
//        } else {
//            RepositoryResponse.Success(
//                body = entity.toDomain()
//            )
//        }
//    }
//
//    override suspend fun updateImageUrl(
//        userId: Uuid,
//        url: String
//    ): RepositoryResponse<User> = dbQuery {
//        val entity = UserEntity.findByIdAndUpdate(id = userId.toJavaUuid()) {
//            it.updatedAt = LocalDateTime.nowUtc()
//            it.imageUrl = url
//        }
//
//        if (entity == null) {
//            RepositoryResponse.Error.NotFound(
//                message = "User with id $userId not found"
//            )
//        } else {
//            RepositoryResponse.Success(
//                body = entity.toDomain()
//            )
//        }
//    }

    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResponse<User> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResponse.Error.NotFound("User with id: $userId not found")

        if (currentUserId == userId) {
            return@dbQuery RepositoryResponse.Success(entity.toAccountDomain())
        }

        val friends = isAcceptedFriendship(currentUserId.toJavaUuid(), userId.toJavaUuid())
        RepositoryResponse.Success(if (friends) entity.toPrivateDomain() else entity.toPublicDomain())
    }


    override suspend fun searchUsers(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<List<User>> = dbQuery {
        val me = userId.toJavaUuid()

        val q = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0)

        val needle = q?.trim()?.takeIf { it.isNotBlank() }

        val where =
            UsersTable.deletedAt.isNull() and (UsersTable.id neq me) and
                    (needle?.let { n ->
                        val pattern = "%$n%"
                        val aliasExistsAndMatches =
                            UsersTable.alias.isNotNull() and ilike(UsersTable.alias, pattern)
                        val aliasMissingSoMatchUsername =
                            UsersTable.alias.isNull() and ilike(UsersTable.username, pattern)
                        aliasExistsAndMatches or aliasMissingSoMatchUsername
                    } ?: Op.TRUE)


        val isFriend = isFriendExpr(me, UsersTable.id)

        val rows = UsersTable
            .select(UsersTable.id, UsersTable.username, UsersTable.alias, UsersTable.bio, UsersTable.imageUrl, UsersTable.role, UsersTable.email, isFriend)
            .where { where }
            .orderBy(
                isFriend to SortOrder.DESC,
                UsersTable.username to SortOrder.ASC
            )
            .limit(safeLimit)
            .offset(safeOffset.toLong())
            .toList()

        val list: List<User> = rows.map { row ->
            val isFriend = row[isFriend]
            if (isFriend) row.rowToPrivateResult() else row.rowToPublicResult()
        }

        RepositoryResponse.Success(list)
    }

    override suspend fun getMyAccount(userId: Uuid): RepositoryResponse<AccountUser> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid()) ?: return@dbQuery RepositoryResponse.Error.NotFound("User with id: $userId not found")
        RepositoryResponse.Success(entity.toAccountDomain())
    }

    override suspend fun updateMyAccount(): RepositoryResponse<AccountUser> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResponse<AccountUser> = dbQuery {
        val entity = UserEntity.findByIdAndUpdate(id = userId.toJavaUuid()) {
            it.updatedAt = LocalDateTime.nowUtc()
            it.imageUrl = url
        }

        if (entity == null) {
            RepositoryResponse.Error.NotFound(
                message = "User with id $userId not found"
            )
        } else {
            RepositoryResponse.Success(
                body = entity.toAccountDomain()
            )
        }
    }

    private fun ResultRow.rowToPublicResult() : PublicUser = PublicUser(
        uuid = this.userUuid(),
        alias = this.displayName(),
        bio = this[UsersTable.bio],
        imageUrl = this[UsersTable.imageUrl]
    )
    private fun ResultRow.rowToPrivateResult() : PrivateUser = PrivateUser(
        uuid = this.userUuid(),
        username = this[UsersTable.username],
        alias = this.displayName(),
        bio = this[UsersTable.bio],
        imageUrl = this[UsersTable.imageUrl],
        role = this[UsersTable.role],
        email = this[UsersTable.email]
    )

    private fun ResultRow.rowToAccountResult() : AccountUser = AccountUser(
        uuid = this.userUuid(),
        username = this[UsersTable.username],
        alias = this.displayName(),
        bio = this[UsersTable.bio],
        imageUrl = this[UsersTable.imageUrl],
        role = this[UsersTable.role],
        email = this[UsersTable.email],
        createdAt = this[UsersTable.createdAt].toInstant(),
        updatedAt = this[UsersTable.updatedAt].toInstant(),
        deletedAt = this[UsersTable.deletedAt]?.toInstant()
    )

    private fun ResultRow.userUuid() =
        this[UsersTable.id].value.toKotlinUuid()

    private fun ResultRow.displayName(): String =
        this[UsersTable.alias] ?: this[UsersTable.username]

    private fun isFriendExpr(me: UUID, userIdExpr: Column<EntityID<UUID>>, aliasName: String = "is_friend" ) = exists(
        FriendshipsTable
            .selectAll()
            .where {
                FriendshipsTable.deletedAt.isNull() and (
                        FriendshipsTable.status eq FriendshipStatus.ACCEPTED
                ) and (
                        (FriendshipsTable.requesterId eq me and(FriendshipsTable.receiverId eq userIdExpr)) or (
                                FriendshipsTable.receiverId eq me and(FriendshipsTable.requesterId eq userIdExpr)
                        )
                )
            }
    ).alias(aliasName)
    private fun isAcceptedFriendship(me: UUID, other: UUID): Boolean {
        return FriendshipsTable
            .selectAll()
            .where {
                FriendshipsTable.deletedAt.isNull() and
                        (FriendshipsTable.status eq FriendshipStatus.ACCEPTED) and
                        (
                                (FriendshipsTable.requesterId eq me and (FriendshipsTable.receiverId eq other)) or
                                        (FriendshipsTable.receiverId eq me and (FriendshipsTable.requesterId eq other))
                                )
            }
            .limit(1)
            .any()
    }
}