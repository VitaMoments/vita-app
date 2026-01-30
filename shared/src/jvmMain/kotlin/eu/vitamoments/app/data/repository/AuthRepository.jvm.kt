package eu.vitamoments.app.data.repository

import kotlinx.datetime.LocalDateTime
import eu.vitamoments.app.config.JWTConfig
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.entities.RefreshTokenEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.entity.fromAuthToken
import eu.vitamoments.app.data.mapper.entity.toAccountDomain
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.domain.api.ErrorCode
import eu.vitamoments.app.data.tables.RefreshTokensTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.PasswordHasher
import eu.vitamoments.app.dbHelpers.dbQuery
import org.jetbrains.exposed.v1.core.eq


class JVMAuthRepository() : ServerAuthRepository {
    val jwtConfig : JWTConfig = JWTConfigLoader.loadOrThrow()
    override suspend fun login(
        email: String,
        password: String
    ): RepositoryResult<AuthSession> = dbQuery {
        val userEntity = findUserByCredentials(email, password) ?: return@dbQuery RepositoryResult.Error(RepositoryError.Unauthorized(message = "Username/password combination not found"))

        val accessToken = jwtConfig.generateAccessToken(userEntity.toAccountDomain())
        val refreshToken = jwtConfig.generateRefreshToken()

        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResult.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = userEntity.toAccountDomain()
            )
        )
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): RepositoryResult<AuthSession> = dbQuery {

        val errors = mutableListOf<RepositoryError.FieldError>()

        if (usernameExists(username)) {
            errors += RepositoryError.FieldError(
                field = "username",
                message = "Username already exists"
            )
        }
        if (emailExists(email)) {
            errors += RepositoryError.FieldError(
                field = "email",
                message = "Email already exists"
            )
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResult.Error(
                RepositoryError.Conflict(
                    errors = errors
                ),
            )
        }

        val hashedPassword = PasswordHasher.hashPassword(password)
        val userEntity = UserEntity.new {
            this.username = username
            this.email = email
            this.password = hashedPassword
        }
        val user = userEntity.toAccountDomain()

        val refreshToken = jwtConfig.generateRefreshToken()
        val accessToken = jwtConfig.generateAccessToken(user)

        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResult.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = user
            )
        )
    }

    override suspend fun logout(refreshToken: String): RepositoryResult<Unit> = dbQuery {
        val tokenEntity = RefreshTokenEntity
            .find { RefreshTokensTable.refreshToken eq refreshToken }
            .firstOrNull()
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound(
                    message = "Refresh token not found",
                    code = ErrorCode.REFRESH_TOKEN_NOT_FOUND
                )
            )
        tokenEntity.revokedAt = LocalDateTime.nowUtc()
        RepositoryResult.Success(Unit)
    }

    override suspend fun refresh(refreshToken: String?): RepositoryResult<AuthSession> = dbQuery {
        if (refreshToken.isNullOrEmpty()) return@dbQuery RepositoryResult.Error(
            RepositoryError.NotFound(
                message = "Refresh token not found",
                code = ErrorCode.REFRESH_TOKEN_NOT_FOUND
            )
        )
        val refreshTokenEntity = RefreshTokenEntity.find { RefreshTokensTable.refreshToken eq refreshToken }
            .firstOrNull()
            ?.takeIf {
                it.revokedAt == null && !it.toDomain().isExpired
            } ?: return@dbQuery RepositoryResult.Error(
            RepositoryError.Unauthorized(
                message = "Refresh token not valid",
                code = ErrorCode.REFRESH_TOKEN_NOT_FOUND
            )
        )

        val userEntity = UserEntity.find { UsersTable.id eq refreshTokenEntity.user.id }.firstOrNull() ?: return@dbQuery RepositoryResult.Error(
            RepositoryError.NotFound(
                message = "Refresh token not valid",
                code = ErrorCode.REFRESH_TOKEN_NOT_VALID
            )
        )

        refreshTokenEntity.revokedAt = LocalDateTime.nowUtc()

        val accessToken = jwtConfig.generateAccessToken(userEntity.toAccountDomain())
        val refreshToken = jwtConfig.generateRefreshToken()
        RefreshTokenEntity.fromAuthToken(refreshToken, userEntity)

        RepositoryResult.Success(
            body = AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = userEntity.toAccountDomain()
            )
        )
    }

    @Deprecated(
        message = "Not supported here. Use usersRepo + authentication module.",
        level = DeprecationLevel.WARNING
    )
    override suspend fun session(): RepositoryResult<AuthSession> =
        throw UnsupportedOperationException("Not supported here; use usersRepo + authentication module")

    suspend fun validRefreshToken(refreshToken: String) = dbQuery {
        RefreshTokenEntity.find { RefreshTokensTable.refreshToken eq refreshToken }
            .firstOrNull()
        ?.takeIf {
            it.revokedAt == null && !it.toDomain().isExpired
        } ?: return@dbQuery null

    }
    suspend fun usernameExists(username: String) : Boolean = dbQuery { UserEntity.find { UsersTable.username eq username }.singleOrNull() != null  }
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