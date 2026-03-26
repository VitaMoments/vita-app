package eu.vitamoments.app.data.models.domain.daily

import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.QuestionType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DailyQuestion(
    val questionItemId: Uuid,
    val questionId: Uuid,
    val question: String,
    val questionType: QuestionType,
    val categories: List<FeedCategory> = emptyList(),
    val minTime: String? = null,
    val maxTime: String? = null,
    val answers: List<String>? = null,
    val questionDate: String,
)

