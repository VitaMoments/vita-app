@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface TimeLineRepository {
    suspend fun createPost(userId: Uuid, content: JsonObject) : RepositoryResponse<TimeLinePost>
    suspend fun getTimeLine(userId: Uuid, limit: Int, offset: Long) : RepositoryResponse<List<TimeLinePost>>
}