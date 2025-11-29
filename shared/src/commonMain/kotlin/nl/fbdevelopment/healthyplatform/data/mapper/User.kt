@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.mapper

import io.ktor.server.util.url
import nl.fbdevelopment.healthyplatform.data.models.domain.user.PublicUser
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.data.models.dto.user.PublicUserDto
import nl.fbdevelopment.healthyplatform.data.models.dto.user.UserDto
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

fun User.toDto() : UserDto = UserDto(
    uuid = this.uuid,
    email = this.email,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt
)

fun User.toPublicDto() : PublicUserDto = PublicUserDto(
    uuid = this.uuid,
    email = this.email,
    imageUrl = this.imageUrl
)

fun User.toPublicUser() : PublicUser = PublicUser(
    uuid = this.uuid,
    email = this.email,
    imageUrl = this.imageUrl
)

fun UserDto.toDomain(): User = User(
    uuid = this.uuid,
    email = this.email,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt
)

fun PublicUser.toDto() : PublicUserDto = PublicUserDto(
    uuid = this.uuid,
    email = this.email,
    imageUrl = this.imageUrl
)

fun PublicUserDto.toDomain() : PublicUser = PublicUser(
    uuid = this.uuid,
    email = this.email,
    imageUrl = this.imageUrl
)