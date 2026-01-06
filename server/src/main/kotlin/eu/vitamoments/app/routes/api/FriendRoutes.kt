package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.mapper.extension_functions.respondRepositoryResponse
import eu.vitamoments.app.data.mapper.toDto
import eu.vitamoments.app.data.mapper.utils.toDto
import eu.vitamoments.app.data.models.dto.user.AcceptFriendInviteDto
import eu.vitamoments.app.data.models.dto.user.DeclineFriendInviteDto
import eu.vitamoments.app.data.models.dto.user.FriendInviteDto
import eu.vitamoments.app.data.models.dto.user.RemoveFriendshipDto
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

            call.respondRepositoryResponse(page, HttpStatusCode.OK) { result ->
                result.toDto { it.toDto() }
            }
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

            call.respondRepositoryResponse(page, HttpStatusCode.OK) { result ->
                result.toDto { it.toDto() }
            }
        }

        post("/invite") {
            val userId = call.requireUserId()
            val dto: FriendInviteDto = call.receive()

            val result = friendRepo.invite(userId, dto.friendId)
            call.respondRepositoryResponse(result) { it.toDto() }
        }

        post("/accept") {
            val userId = call.requireUserId()
            val dto: AcceptFriendInviteDto = call.receive()

            val result = friendRepo.accept(userId, dto.friendId)
            call.respondRepositoryResponse(result) { it.toDto() }
        }

        post("/reject") {
            val userId = call.requireUserId()
            val dto: DeclineFriendInviteDto = call.receive()

            val result = friendRepo.decline(userId, dto.friendId)
            call.respondRepositoryResponse(result) { it.toDto() }
        }

        post("/revoke") {
            val userId = call.requireUserId()
            val dto: RemoveFriendshipDto = call.receive()

            val result = friendRepo.delete(userId, dto.friendId)
            call.respondRepositoryResponse(result) { it.toDto() }
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

            call.respondRepositoryResponse(page, HttpStatusCode.OK) { result ->
                result.toDto { it.toDto() }
            }
        }
    }
}