package eu.vitamoments.app.data.models.domain.daily

import kotlinx.serialization.Serializable

@Serializable
data class DailyQuestionAnswerResult(
    val accepted: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val alreadyAnsweredToday: Boolean,
)

