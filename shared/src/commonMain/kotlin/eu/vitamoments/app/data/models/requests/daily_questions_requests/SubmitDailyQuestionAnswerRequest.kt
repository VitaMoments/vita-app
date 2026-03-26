package eu.vitamoments.app.data.models.requests.daily_questions_requests

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SubmitDailyQuestionAnswerRequest(
    val questionItemId: Uuid,
    val answerText: String? = null,
    val selectedAnswer: String? = null,
    val answerDocument: RichTextDocument? = null,
)
