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
import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest
import eu.vitamoments.app.data.models.requests.handleResult
import eu.vitamoments.app.data.models.requests.respondError
import eu.vitamoments.app.data.models.requests.respondRepository
import eu.vitamoments.app.data.repository.RepositoryResult
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.UserRepository
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.authRoutes() {
    val authRepo: ServerAuthRepository by inject()
    val userRepo: UserRepository by inject()

    route("/auth") {
        post("/register") {
            val request: RegistrationRequest = call.receive()
            val result: RepositoryResult<AuthSession> =
                authRepo.register(username = request.username, email = request.email, password = request.password)

            call.handleResult(
                result = result,
                onSuccess = {session ->
                    call.setAuthCookies(session)
                    call.respond(status = HttpStatusCode.Created, session.user)
                }
            )
        }

        post("/login") {
            val request: LoginRequest = call.receive()
            val result: RepositoryResult<AuthSession> =
                authRepo.login(email = request.email, password = request.password)

            call.handleResult(
                result = result,
                onSuccess = { session ->
                    call.setAuthCookies(session)
                    call.respond(status = HttpStatusCode.OK, session.user)
                }
            )
        }

        post("/refresh") {
            val refresh = call.requireRefreshToken()
            val result: RepositoryResult<AuthSession> = authRepo.refresh(refresh)

            call.handleResult(
                result = result,
                onSuccess = {session ->
                    call.setAuthCookies(session)
                    call.respond(HttpStatusCode.NoContent)
                }
            )
        }

        post("/logout") {
            val refreshToken = call.requireRefreshToken()
            authRepo.logout(refreshToken)
            call.clearAuthCookies()
            call.respond(HttpStatusCode.NoContent)
        }

        authenticate("cookie-jwt-authentication") {
            get("/session") {
                val userId: Uuid = call.requireUserId()
                val result: RepositoryResult<AccountUser> = userRepo.getMyAccount(userId = userId)
                call.handleResult(result)
            }
        }
    }
}
