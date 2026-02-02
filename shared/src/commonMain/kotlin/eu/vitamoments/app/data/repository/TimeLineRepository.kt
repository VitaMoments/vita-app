package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.enums.TimeLineFeed
import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import kotlinx.serialization.json.JsonElement
import kotlin.uuid.Uuid

interface TimeLineRepository {
    suspend fun createPost(userId: Uuid, content: JsonElement) : RepositoryResult<TimelineItem>
    suspend fun getTimeLine(userId: Uuid, feed: TimeLineFeed, limit: Int, offset: Long) : RepositoryResult<List<TimelineItem>>
}