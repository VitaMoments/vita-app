package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.models.requests.respondError
import eu.vitamoments.app.data.models.requests.respondRepository
import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.UserRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.userRoutes() {
    val userRepo: UserRepository by inject()

    route("/users") {
        get("/search") {
            val currentUserId = call.requireUserId()
            val query = call.request.queryParameters["query"]?.trim()?.takeIf { it.isNotBlank() }
            val safeLimit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 20).coerceIn(1, 50)
            val safeOffset = (call.request.queryParameters["offset"]?.toIntOrNull() ?: 0).coerceAtLeast(0)

            val result = userRepo.searchUsers(
                currentUserId = currentUserId,
                query = query,
                limit = safeLimit,
                offset = safeOffset
            )
            call.respondRepository(result)
        }

        get("/{userId}") {
            val currentUserId = call.requireUserId()
            val rawUserId = call.parameters["userId"]
                ?: return@get call.respondError(
                    RepositoryError.BadRequest(
                        errors = listOf(RepositoryError.FieldError("userId", "Missing userId path parameter"))
                    )
                )

            val targetUserId = runCatching { Uuid.parse(rawUserId) }.getOrNull()
                ?: return@get call.respondError(
                    RepositoryError.BadRequest(
                        errors = listOf(RepositoryError.FieldError("userId", "Invalid userId format"))
                    )
                )

            val result = userRepo.getUser(currentUserId = currentUserId, userId = targetUserId)
            call.respondRepository(result)
        }
    }
}