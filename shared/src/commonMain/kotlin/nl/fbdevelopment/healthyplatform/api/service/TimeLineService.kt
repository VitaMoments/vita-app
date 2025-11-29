@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.JsonObject
import nl.fbdevelopment.healthyplatform.data.models.dto.message.CreateTimeLinePostDto
import kotlin.uuid.ExperimentalUuidApi

interface TimeLineService {
    suspend fun createPost(body: CreateTimeLinePostDto): HttpResponse
    suspend fun getTimeline(limit: Int, offset: Long): HttpResponse
}