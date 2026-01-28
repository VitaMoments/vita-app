package eu.vitamoments.app.data.models.requests.timeline_requests

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import kotlinx.serialization.Serializable

@Serializable
data class CreateTimelineItemRequest(
    val document: RichTextDocument
)
