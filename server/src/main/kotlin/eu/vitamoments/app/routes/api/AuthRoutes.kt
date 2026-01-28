@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.routes.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import eu.vitamoments.app.api.helpers.clearAuthCookies
import eu.vitamoments.app.api.helpers.requireRefreshToken
import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.api.helpers.setAuthCookies
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest
import eu.vitamoments.app.data.models.requests.respondRepository
import eu.vitamoments.app.data.repository.RepositoryResponse
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.UserRepository
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.authRoutes() {
    val authRepo : ServerAuthRepository by inject()
    val userRepo : UserRepository by inject()

    route("/auth") {
        post("/register") {
            val request: RegistrationRequest = call.receive()
            val result: RepositoryResponse<AuthSession> = authRepo.register(username = request.username, email = request.email, password = request.password)

            when (result) {
                is RepositoryResponse.Success -> {
                    val session = result.body
                    call.setAuthCookies(session)
                }
                else -> result
            }
            call.respond(result)
            call.respondRepository(result)
        }

        post("/login") {
            val request : LoginRequest = call.receive()
            val result: RepositoryResponse<AuthSession> = authRepo.login(email = request.email, password = request.password)
            if (result is RepositoryResponse.Success) {
                val session = result.body
                call.setAuthCookies(session)
                call.respondRepository(RepositoryResponse.Success(result.body.user))
            } else {
                call.respond(result)
            }
        }

        post("/refresh") {
            val refresh = call.requireRefreshToken()
            val result: RepositoryResponse<AuthSession> = authRepo.refresh(refresh)

            if (result is RepositoryResponse.Success) {
                val session = result.body
                call.setAuthCookies(session)
                call.respondRepository(RepositoryResponse.Success(result.body.user))
            } else {
                call.respond(result)
            }
        }

        post("/logout") {
            val refreshToken = call.requireRefreshToken()
            authRepo.logout(refreshToken)
//            force logout, refresh token will expire sometime
            call.clearAuthCookies()
            call.respond(HttpStatusCode.OK)
        }

        authenticate("cookie-jwt-authentication") {
            get("/session") {
                val userId : Uuid = call.requireUserId()
                val result: RepositoryResponse<AccountUser> = userRepo.getMyAccount(userId = userId)
                call.respondRepository(result)
            }
        }
    }
}