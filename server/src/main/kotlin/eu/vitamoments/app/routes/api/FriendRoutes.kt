package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.models.requests.friendship_requests.InviteFriendshipRequest
import eu.vitamoments.app.data.models.requests.friendship_requests.UpdateFriendshipRequest
import eu.vitamoments.app.data.models.requests.respondRepository
import eu.vitamoments.app.data.repository.FriendRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.friendRoutes() {
    val friendRepo: FriendRepository by inject()

    route("/friends") {
        get {
            val userId = call.requireUserId()

            val query = call.request.queryParameters["query"]?.trim()?.takeIf { it.isNotBlank() } ?: ""
            val safeLimit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 20).coerceIn(1, 50)
            val safeOffset = (call.request.queryParameters["offset"]?.toIntOrNull() ?: 0).coerceAtLeast(0)

            val page = friendRepo.searchFriends(
                userId = userId,
                query = query,
                limit = safeLimit,
                offset = safeOffset
            )
            call.respondRepository(page)
        }

        get("/new") {
            val userId = call.requireUserId()

            val query = call.request.queryParameters["query"]?.trim()?.takeIf { it.isNotBlank() } ?: ""
            val limit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 20).coerceIn(1, 50)
            val offset = (call.request.queryParameters["offset"]?.toIntOrNull() ?: 0).coerceAtLeast(0)

            val page = friendRepo.searchNewFriends(
                userId = userId,
                query = query,
                limit = limit,
                offset = offset
            )

            call.respondRepository(page)
        }

        post("/invite") {
            val userId = call.requireUserId()
            val request: InviteFriendshipRequest = call.receive()

            val result = friendRepo.invite(userId, request.userId)
            call.respondRepository(result)
        }

        post("/accept") {
            val userId = call.requireUserId()
            val request: UpdateFriendshipRequest = call.receive()

            val result = friendRepo.accept(userId, request.friendshipId)
            call.respondRepository(result)
        }

        post("/reject") {
            val userId = call.requireUserId()
            val request: UpdateFriendshipRequest = call.receive()

            val result = friendRepo.decline(userId, request.friendshipId)
            call.respondRepository(result)
        }

        post("/revoke") {
            val userId = call.requireUserId()
            val request: UpdateFriendshipRequest = call.receive()

            val result = friendRepo.delete(userId, request.friendshipId)
            call.respondRepository(result)
        }

        get("/invites") {
            val userId = call.requireUserId()

            val query = call.request.queryParameters["query"]?.trim()?.takeIf { it.isNotBlank() } ?: ""
            val safeLimit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 20).coerceIn(1, 50)
            val safeOffset = (call.request.queryParameters["offset"]?.toIntOrNull() ?: 0).coerceAtLeast(0)

            val page = friendRepo.friendRequests(
                userId = userId,
                query = query,
                limit = safeLimit,
                offset = safeOffset
            )

            call.respondRepository(page)
        }
    }
}