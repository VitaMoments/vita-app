@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.models.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.entities.RefreshTokenEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.dbHelpers.PasswordHasher
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.mappers.toUtcLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@Serializable
object TestUsers {
    private fun nowUtc() = LocalDateTime.Companion.nowUtc()
    private val jwtConfig = JWTConfigLoader.loadOrThrow()

    suspend fun default(
        email: String = "default@example.com",
        password: String = "password"
    ) : User = dbQuery {
        val userEntity = insertUser(email, password)
        insertRefreshToken(userEntity)
        return@dbQuery userEntity.toDomain()
    }

    suspend fun defaultAsAuthSession(
        email: String = "default@example.com",
        password: String = "password"
    ) : AuthSession = dbQuery {
        val userEntity = insertUser(email = email, password = password)
        val accessToken = jwtConfig.generateAccessToken(userEntity.toDomain())
        val refreshTokenEntity = insertRefreshToken(userEntity)
        AuthSession(accessToken = accessToken, refreshTokenEntity.toDomain(), user = userEntity.toDomain())
    }

    private suspend fun insertUser(email: String, password: String) : UserEntity = dbQuery {
        UserEntity.new {
            this.email = email
            this.password = PasswordHasher.hashPassword(password)
        }
    }
    private suspend fun insertRefreshToken(userEntity: UserEntity) : RefreshTokenEntity = dbQuery {
        val refreshAuthToken = jwtConfig.generateRefreshToken()
        RefreshTokenEntity.new {
            this.refreshToken = refreshAuthToken.token
            this.expiredAt = refreshAuthToken.expiredAt.toUtcLocalDateTime()
            this.user = userEntity
        }
    }
}