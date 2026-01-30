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
import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.requests.handleResult
import eu.vitamoments.app.data.models.requests.timeline_requests.CreateTimelineItemRequest
import eu.vitamoments.app.data.repository.RepositoryResult
import eu.vitamoments.app.data.repository.TimeLineRepository
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.timelineRoutes() {
    val timeLineRepo: TimeLineRepository by inject()

    route("/timeline") {
        post {
            val request : CreateTimelineItemRequest = call.receive()
            val userid: Uuid = call.requireUserId()

            val result: RepositoryResult<TimelineItem> = timeLineRepo.createPost(userid, request.document.content)
            call.handleResult(result, successStatusCode = HttpStatusCode.Created)
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
            val result: RepositoryResult<List<TimelineItem>> = timeLineRepo.getTimeLine(userId, feed,params["limit"]?.toInt() ?: 100, params["offset"]?.toLong() ?: 0)
            call.handleResult(result)
        }

        delete {

        }

        put {

        }
    }
}