@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.routes.api


import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.extension_functions.respondRepositoryResponse
import eu.vitamoments.app.data.mapper.toDto
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import eu.vitamoments.app.data.models.dto.message.CreateTimeLinePostDto
import eu.vitamoments.app.data.repository.FriendRepository
import eu.vitamoments.app.data.repository.RepositoryResponse
import eu.vitamoments.app.data.repository.TimeLineRepository
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.timelineRoutes() {
    val timeLineRepo: TimeLineRepository by inject()
    val friendRepo: FriendRepository by inject()

    route("/timeline") {
        post {
            val createTimeLinePostDto : CreateTimeLinePostDto = call.receive()
            val userid: Uuid = call.requireUserId()

            val result: RepositoryResponse<TimeLinePost> = timeLineRepo.createPost(userid, createTimeLinePostDto.content)

            call.respondRepositoryResponse(result, HttpStatusCode.Created) { timeLinePost -> timeLinePost.toDto()}
        }

        get {
            val params = call.pathParameters
            val userId: Uuid = call.requireUserId()
            val feed = when (call.request.queryParameters["feed"]?.lowercase()) {
                null, "", "friends" -> TimeLineFeed.FRIENDS
                "self" -> TimeLineFeed.SELF
                "discovery" -> TimeLineFeed.DISCOVERY
                "groups" -> TimeLineFeed.GROUPS
                else -> return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid feed. Use: self, friends, discovery, groups"
                )
            }

            val result: RepositoryResponse<List<TimeLinePost>> = timeLineRepo.getTimeLine(userId, feed,params["limit"]?.toInt() ?: 100, params["offset"]?.toLong() ?: 0)
            call.respondRepositoryResponse(result, HttpStatusCode.OK) { list -> list.map { it.toDto() }}
        }

        delete {

        }

        put {

        }
    }
}