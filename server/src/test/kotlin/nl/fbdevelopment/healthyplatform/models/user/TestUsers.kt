@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.models.user

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import nl.fbdevelopment.healthyplatform.config.JWTConfigLoader
import nl.fbdevelopment.healthyplatform.data.entities.RefreshTokenEntity
import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.mapper.enitity.toDomain
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.nowUtc
import nl.fbdevelopment.healthyplatform.data.models.domain.AuthSession
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.dbHelpers.PasswordHasher
import nl.fbdevelopment.healthyplatform.dbHelpers.dbQuery
import nl.fbdevelopment.healthyplatform.mappers.toUtcLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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