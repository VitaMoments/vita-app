package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.api.service.TimeLineService
import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.requests.timeline_requests.CreateTimelineItemRequest
import kotlin.uuid.Uuid

class TimeLineRepositoryImpl(
    private val service: TimeLineService
): TimeLineRepository {
    override suspend fun createPost(userId: Uuid, content: JsonObject) : RepositoryResponse<TimelineItem> {
        val requestBody = CreateTimelineItemRequest(document = RichTextDocument(content = content))
        val response = service.createTimelineItem(body = requestBody)
        return response.toRepositoryResponse<TimelineItem>()
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        feed: TimeLineFeed,
        limit: Int,
        offset: Long
    ): RepositoryResponse<List<TimelineItem>> {
        TODO("Not yet implemented")
    }
}