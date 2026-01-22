package eu.vitamoments.app.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.dto.feed.WriteTimelineItemDto

class TimeLineServiceImpl(val client: HttpClient) : TimeLineService {
    override suspend fun createPost(body: WriteTimelineItemDto): HttpResponse = client.post("/timeline") {
        setBody(body)
    }

    override suspend fun getTimeline(
        limit: Int,
        offset: Long
    ): HttpResponse = client.get("/timeline")

}