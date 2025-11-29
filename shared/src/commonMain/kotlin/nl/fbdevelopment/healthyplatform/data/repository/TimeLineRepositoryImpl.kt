@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.repository

import kotlinx.serialization.json.JsonObject
import nl.fbdevelopment.healthyplatform.api.service.TimeLineService
import nl.fbdevelopment.healthyplatform.data.mapper.toDomain
import nl.fbdevelopment.healthyplatform.data.mapper.toRepositoryResponse
import nl.fbdevelopment.healthyplatform.data.models.domain.message.TimeLinePost
import nl.fbdevelopment.healthyplatform.data.models.dto.message.CreateTimeLinePostDto
import nl.fbdevelopment.healthyplatform.data.models.dto.message.TimeLinePostDto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TimeLineRepositoryImpl(
    private val service: TimeLineService
): TimeLineRepository {
    override suspend fun createPost(userId: Uuid, content: JsonObject) : RepositoryResponse<TimeLinePost> {
        val dto = CreateTimeLinePostDto(content = content)
        val response = service.createPost(dto)
        return response.toRepositoryResponse<TimeLinePostDto, TimeLinePost> { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        limit: Int,
        offset: Long
    ): RepositoryResponse<List<TimeLinePost>> {
        TODO("Not yet implemented")
    }
}