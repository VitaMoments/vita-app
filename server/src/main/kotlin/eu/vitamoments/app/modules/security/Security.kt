@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.modules.security


import io.ktor.server.response.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import eu.vitamoments.app.config.JWTConfig
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.UserRepository
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi

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