@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import kotlinx.datetime.LocalDateTime
import eu.vitamoments.app.config.JWTConfig
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.entities.RefreshTokenEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.enitity.fromAuthToken
import eu.vitamoments.app.data.mapper.enitity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.tables.RefreshTokensTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.PasswordHasher
import eu.vitamoments.app.dbHelpers.dbQuery
import org.jetbrains.exposed.v1.core.eq
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi


class JVMAuthRepository() : ServerAuthRepository {
    val jwtConfig : JWTConfig = JWTConfigLoader.loadOrThrow()
    override suspend fun login(
        email: String,
        password: String
    ): RepositoryResponse<AuthSession> = dbQuery {
        val userEntity = findUserByCredentials(email, password) ?: return@dbQuery RepositoryResponse.Error.Unauthorized("email / password combination not found")

        val accessToken = jwtConfig.generateAccessToken(userEntity.toDomain())
        val refreshToken = jwtConfig.generateRefreshToken()

        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResponse.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = userEntity.toDomain()
            )
        )
    }

    override suspend fun register(
        email: String,
        password: String
    ): RepositoryResponse<AuthSession> = dbQuery {
        if (emailExists(email)) return@dbQuery RepositoryResponse.Error.Conflict(key = "email", "email already exists")

        val hashedPassword = PasswordHasher.hashPassword(password)
        val userEntity = UserEntity.new {
            this.email = email
            this.password = hashedPassword
        }
        val user = userEntity.toDomain()

        val refreshToken = jwtConfig.generateRefreshToken()
        val accessToken = jwtConfig.generateAccessToken(user)

        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResponse.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = user
            )
        )
    }

    override suspend fun logout(refreshToken: String): RepositoryResponse<Boolean> = dbQuery {
        val tokenEntity = RefreshTokenEntity.find { RefreshTokensTable.refreshToken eq refreshToken }.firstOrNull() ?: return@dbQuery RepositoryResponse.Error.NotFound("RefreshToken not found")
        tokenEntity.revokedAt = LocalDateTime.nowUtc()
        RepositoryResponse.Success(body = true)
    }

    override suspend fun refresh(refreshToken: String?): RepositoryResponse<AuthSession> = dbQuery {
        if (refreshToken.isNullOrEmpty()) return@dbQuery RepositoryResponse.Error.Unauthorized("No refresh token provided")
        val refreshTokenEntity = RefreshTokenEntity.find { RefreshTokensTable.refreshToken eq refreshToken }
            .firstOrNull()
            ?.takeIf {
                it.revokedAt == null && !it.toDomain().isExpired
            } ?: return@dbQuery RepositoryResponse.Error.Unauthorized("RefreshToken is not valid")

        val userEntity = UserEntity.find { UsersTable.id eq refreshTokenEntity.user.id }.firstOrNull() ?: return@dbQuery RepositoryResponse.Error.Internal("RefreshToken is not linked to an account")

        refreshTokenEntity.revokedAt = LocalDateTime.nowUtc()

        val accessToken = jwtConfig.generateAccessToken(userEntity.toDomain())
        val refreshToken = jwtConfig.generateRefreshToken()
        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResponse.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = userEntity.toDomain()
            )
        )
    }

    @Deprecated(
        message = "Not supported here. Use usersRepo + authentication module.",
        level = DeprecationLevel.WARNING
    )
    override suspend fun session(): RepositoryResponse<AuthSession> =
        throw UnsupportedOperationException("Not supported here; use usersRepo + authentication module")

    suspend fun validRefreshToken(refreshToken: String) = dbQuery {
        RefreshTokenEntity.find { RefreshTokensTable.refreshToken eq refreshToken }
            .firstOrNull()
        ?.takeIf {
            it.revokedAt == null && !it.toDomain().isExpired
        } ?: return@dbQuery null

    }
    suspend fun emailExists(email: String) : Boolean = dbQuery { UserEntity.find { UsersTable.email eq email }.singleOrNull() != null }
    suspend fun findUserByCredentials(email: String, password: String) : UserEntity? = dbQuery {
        UserEntity.find { UsersTable.email eq email }
            .firstOrNull()
            ?.takeIf {
                PasswordHasher.verifyPassword(
                    password = password,
                    hashed = it.password
                )
            }
    }
}