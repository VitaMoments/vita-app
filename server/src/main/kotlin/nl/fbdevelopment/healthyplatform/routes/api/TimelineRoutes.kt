@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.routes.api


import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import nl.fbdevelopment.healthyplatform.api.helpers.requireUserId
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.respondRepositoryResponse
import nl.fbdevelopment.healthyplatform.data.mapper.toDto
import nl.fbdevelopment.healthyplatform.data.models.domain.message.TimeLinePost
import nl.fbdevelopment.healthyplatform.data.models.dto.message.CreateTimeLinePostDto
import nl.fbdevelopment.healthyplatform.data.repository.RepositoryResponse
import nl.fbdevelopment.healthyplatform.data.repository.TimeLineRepository
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.timelineRoutes() {
    val timeLineRepo: TimeLineRepository by inject()

    route("/timeline") {
        post {
            val createTimeLinePostDto : CreateTimeLinePostDto = call.receive()
            val userid: Uuid = call.requireUserId()

            val result: RepositoryResponse<TimeLinePost> = timeLineRepo.createPost(userid, createTimeLinePostDto.content)

            call.respondRepositoryResponse(result, HttpStatusCode.Created) { timeLinePost -> timeLinePost.toDto()}
        }

        get {

        }

        delete {

        }

        put {

        }
    }
}