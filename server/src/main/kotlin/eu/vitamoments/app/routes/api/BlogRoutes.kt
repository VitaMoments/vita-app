package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.entities.BlogItemEntity
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.requests.feed_requests.CreateBlogItemRequest
import eu.vitamoments.app.data.models.requests.respondError
import eu.vitamoments.app.data.models.requests.respondRepository
import eu.vitamoments.app.data.repository.BlogRepository
import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.RepositoryResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.getValue
import kotlin.uuid.Uuid

fun Route.blogRoutes() {
    val repo: BlogRepository by inject()

    route("/blogs") {
        post {
            val request : CreateBlogItemRequest = call.receive()
            val userid: Uuid = call.requireUserId()

            val result: RepositoryResult<BlogItem> = repo.create(
                userId = userid,
                title = request.title,
                subtitle = request.subtitle,
                categories = request.categories,
                coverImageUrl = request.coverImageUrl,
                coverImageAlt = request.coverImageAlt,
                document = request.document,
                privacy = request.privacy,
                status = request.status
            )

            call.respondRepository(rr = result, successStatusCode = HttpStatusCode.Created)
        }
    }
}