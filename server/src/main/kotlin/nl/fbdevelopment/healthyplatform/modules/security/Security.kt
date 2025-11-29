@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.modules.security


import io.ktor.server.response.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import nl.fbdevelopment.healthyplatform.api.helpers.refreshToken
import nl.fbdevelopment.healthyplatform.config.JWTConfig
import nl.fbdevelopment.healthyplatform.data.entities.RefreshTokenEntity
import nl.fbdevelopment.healthyplatform.data.repository.AuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.JVMAuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.ServerAuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.UserRepository
import nl.fbdevelopment.healthyplatform.data.tables.RefreshTokensTable
import nl.fbdevelopment.healthyplatform.dbHelpers.dbQuery
import org.jetbrains.exposed.v1.core.eq
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Application.configureSecurity() {
    val jwtConfig: JWTConfig by inject()
    val authRepo: ServerAuthRepository by inject()
    val userRepo: UserRepository by inject()

    authentication {
        jwt("cookie-jwt-authentication") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)

            authHeader { call ->
                val accessToken = call.request.cookies["access_token"]
                if (accessToken != null) {
                    HttpAuthHeader.Single("Bearer", accessToken)
                } else {
                    null
                }
            }
            validate { credential ->
                val userUuid = credential.payload.getClaim(JWTConfig.USER_KEY).asString()
                if (userUuid != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is invalid or expired")
            }
        }
    }
}