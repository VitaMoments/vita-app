@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api.service

import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.dto.feed.WriteTimelineItemDto
import kotlin.uuid.ExperimentalUuidApi

interface TimeLineService {
    suspend fun createPost(body: WriteTimelineItemDto): HttpResponse
    suspend fun getTimeline(limit: Int, offset: Long): HttpResponse
}