package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.models.domain.address.Address
import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.daily.StreakSummary
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import kotlinx.serialization.Serializable

@Serializable
data class UserWithContext(
    val user: User,
    val streak: StreakSummary = StreakSummary(),
    val friendship: Friendship? = null,
    val timeline: PagedResult<TimelineItem>? = null,
    val addresses: PagedResult<Address>? = null
)
