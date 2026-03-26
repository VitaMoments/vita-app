package eu.vitamoments.app.data.models.domain.daily

import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class StreakSummary(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    @Serializable(with = InstantSerializer::class) val lastAnsweredAt: Instant? = null
)
