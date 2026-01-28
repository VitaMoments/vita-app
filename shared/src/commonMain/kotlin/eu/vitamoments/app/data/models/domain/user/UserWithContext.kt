package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import kotlinx.serialization.Serializable

@Serializable
data class UserWithContext(
    val user: User,
    val friendship: Friendship? = null,
    val blogs: PagedResult<BlogItem>? = null,
    val timeline: PagedResult<TimelineItem>? = null,
)
