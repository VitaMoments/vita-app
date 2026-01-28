package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.enums.TimeLineFeed
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.requests.timeline_requests.CreateTimelineItemRequest
import io.ktor.client.request.parameter

class TimeLineServiceImpl(val client: HttpClient) : TimeLineService {
    override suspend fun createTimelineItem(body: CreateTimelineItemRequest): HttpResponse = client.post("/timeline") {
        setBody(body)
    }

    override suspend fun getTimeline(
        limit: Int,
        offset: Long,
        feed: TimeLineFeed
    ): HttpResponse = client.get("/timeline") {
        parameter("limit", limit)
        parameter("offset", offset)
        parameter("feed", feed.name)
    }
}