package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.tables.UsersTable
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


fun UserEntity.toDomainForViewer(viewerUuid: Uuid, status: FriendshipStatus? = null) : User {
    val authorUuid = this.uuid()
    return when {
        authorUuid == viewerUuid -> this.toAccountDomain()
        status == FriendshipStatus.ACCEPTED -> this.toPrivateDomain()
        else -> this.toPublicDomain()
    }
}

fun UserEntity.toPrivateDomain() : PrivateUser = PrivateUser(
    uuid = this.uuid(),
    username = this.username,
    email = this.email,
    displayName = this.displayName(),
    bio = this.bio,
    role = this.role,
    imageUrl = this.imageUrl
)
fun UserEntity.toPublicDomain() : PublicUser = PublicUser(
    uuid = this.uuid(),
    displayName = this.displayName(),
    bio = this.bio,
    imageUrl = this.imageUrl
)

fun UserEntity.toAccountDomain() : AccountUser = AccountUser(
    uuid = this.uuid(),
    username = this.username,
    alias = this.alias,
    bio = this.bio,
    role = this.role,
    email = this.email,
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    imageUrl = this.imageUrl
)

fun ResultRow.rowToPublicResult() : PublicUser = PublicUser(
    uuid = this.userUuid(),
    displayName = this.displayName(),
    bio = this[UsersTable.bio],
    imageUrl = this[UsersTable.imageUrl]
)
fun ResultRow.rowToPrivateResult() : PrivateUser = PrivateUser(
    uuid = this.userUuid(),
    username = this[UsersTable.username],
    displayName = this.displayName(),
    bio = this[UsersTable.bio],
    imageUrl = this[UsersTable.imageUrl],
    role = this[UsersTable.role],
    email = this[UsersTable.email]
)
fun ResultRow.rowToAccountResult() : AccountUser = AccountUser(
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

private fun UserEntity.displayName(): String =
    this.alias ?: this.username

private fun UserEntity.uuid() =
    this.id.value.toKotlinUuid()