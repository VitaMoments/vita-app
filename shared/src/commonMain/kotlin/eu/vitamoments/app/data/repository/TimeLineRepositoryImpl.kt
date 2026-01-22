package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.api.service.TimeLineService
import eu.vitamoments.app.data.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.toDomain
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.dto.feed.TimelineItemDto
import eu.vitamoments.app.data.models.dto.feed.WriteTimelineItemDto
import kotlin.uuid.Uuid

class TimeLineRepositoryImpl(
    private val service: TimeLineService
): TimeLineRepository {
    override suspend fun createPost(userId: Uuid, content: JsonObject) : RepositoryResponse<TimelineItem> {
        val dto = WriteTimelineItemDto(content = content)
        val response = service.createPost(dto)
        return response.toRepositoryResponse<TimelineItemDto, TimelineItem> { dto ->
            dto.toDomain()
        }
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