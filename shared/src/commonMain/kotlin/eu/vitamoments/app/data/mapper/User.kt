@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.dto.user.PublicUserDto
import eu.vitamoments.app.data.models.dto.user.UserDto
import eu.vitamoments.app.data.models.validation.validate
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

fun User.toDto() : UserDto = UserDto(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt
)


fun User.toPublicDto() : PublicUserDto = PublicUserDto(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl
)

fun User.toPublicUser() : PublicUser = PublicUser(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl
)

fun UserDto.toDomain(): User = User(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt
)

fun PublicUser.toDto() : PublicUserDto = PublicUserDto(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl
)

fun PublicUserDto.toDomain() : PublicUser = PublicUser(
    uuid = this.uuid,
    username = this.username,
    email = this.email,
    role = this.role,
    bio = this.bio,
    alias = this.alias,
    imageUrl = this.imageUrl
)