@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.dto.user.AccountUserDto
import eu.vitamoments.app.data.models.dto.user.PublicUserDto
import eu.vitamoments.app.data.models.dto.user.PrivateUserDto
import eu.vitamoments.app.data.models.dto.user.UserDto
import eu.vitamoments.app.data.models.dto.user.UserWithContextDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

fun User.toDto(): UserDto = when (this) {
    is PublicUser -> this.toDto()
    is PrivateUser -> this.toDto()
    is AccountUser -> this.toDto()
    is UserWithContext -> this.toDto()
}

fun UserDto.toDomain(): User = when (this) {
    is PublicUserDto -> this.toDomain()
    is PrivateUserDto -> this.toDomain()
    is AccountUserDto -> this.toDomain()
    is UserWithContextDto -> this.toDomain()
}


fun UserWithContext.toDto() : UserWithContextDto = UserWithContextDto(
    user = this.user.toDto(),
    friendship = this.friendship?.toDto()
)

fun UserWithContextDto.toDomain(): UserWithContext = UserWithContext(
    user = user.toDomain(),
    friendship = friendship?.toDomain()
)

fun PublicUser.toDto() : PublicUserDto = PublicUserDto(
    uuid = this.uuid,
    displayName = this.alias,
    bio = this.bio,
    imageUrl = this.imageUrl
)

fun PrivateUser.toDto(): PrivateUserDto = PrivateUserDto(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    displayName = this.alias ?: this.username,
    bio = this.bio,
    role = this.role,
    imageUrl = this.imageUrl
)

fun AccountUser.toDto(): AccountUserDto = AccountUserDto(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    alias = this.alias,
    displayName = this.alias ?: this.username,
    bio = this.bio,
    role = this.role,
    imageUrl = this.imageUrl,
    createdAt = this.createdAt.toEpochMilliseconds(),
    updatedAt = this.updatedAt.toEpochMilliseconds(),
    deletedAt = this.deletedAt?.toEpochMilliseconds()
)

fun PublicUserDto.toDomain() : PublicUser = PublicUser(
    uuid = this.uuid,
    alias = this.displayName,
    bio = this.bio,
    imageUrl = this.imageUrl
)

fun PrivateUserDto.toDomain() : PrivateUser = PrivateUser(
    uuid = this.uuid,
    username = this.username,
    alias = if (this.username == this.displayName) null else this.displayName,
    bio = this.bio,
    role = this.role,
    email = this.email
)

fun AccountUserDto.toDomain() : AccountUser = AccountUser(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    alias = this.alias,
    bio = this.bio,
    role = this.role,
    imageUrl = this.imageUrl,
    createdAt = Instant.fromEpochMilliseconds(this.createdAt),
    updatedAt = Instant.fromEpochMilliseconds(this.updatedAt),
    deletedAt = this.deletedAt?.let { Instant.fromEpochMilliseconds(it) },
)