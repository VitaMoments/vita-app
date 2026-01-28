package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.models.requests.timeline_requests.CreateTimelineItemRequest
import io.ktor.client.statement.HttpResponse

interface TimeLineService {
    suspend fun createTimelineItem(body: CreateTimelineItemRequest): HttpResponse
    suspend fun getTimeline(limit: Int, offset: Long, feed: TimeLineFeed): HttpResponse
}