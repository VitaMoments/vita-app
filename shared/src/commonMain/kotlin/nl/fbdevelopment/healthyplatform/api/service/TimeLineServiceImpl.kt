package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import nl.fbdevelopment.healthyplatform.data.models.dto.message.CreateTimeLinePostDto

class TimeLineServiceImpl(val client: HttpClient) : TimeLineService {
    override suspend fun createPost(body: CreateTimeLinePostDto): HttpResponse = client.post("/timeline") {
        setBody(body)
    }

    override suspend fun getTimeline(
        limit: Int,
        offset: Long
    ): HttpResponse = client.get("/timeline")

}