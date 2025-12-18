@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package eu.vitamoments.app.models.posts

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import eu.vitamoments.app.data.models.dto.message.CreateTimeLinePostDto
import eu.vitamoments.app.dbHelpers.dbQuery
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

object TestTimeLinePosts {
    suspend fun generateCreateTimelineDto() = dbQuery{
        val doc: JsonObject = Json.parseToJsonElement(
            """
        {
          "type": "doc",
          "content": [
            { "type": "paragraph", "content": [{ "type": "text", "text": "Regel 1" }] },
            { "type": "paragraph" },
            { "type": "paragraph" },
            { "type": "paragraph", "content": [{ "type": "text", "text": "Regel 4" }] }
          ]
        }
      """.trimIndent()
        ).jsonObject
        CreateTimeLinePostDto(content = doc)
    }
}