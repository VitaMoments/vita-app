package eu.vitamoments.app.data.models.requests.feed_requests

import eu.vitamoments.app.data.models.domain.feed.FeedItem
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFeedItemRequest(
    val item: FeedItem
)
