package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.daily.DailyQuestion
import eu.vitamoments.app.data.models.domain.daily.DailyQuestionAnswerResult
import eu.vitamoments.app.data.models.requests.daily_questions_requests.SubmitDailyQuestionAnswerRequest
import kotlin.uuid.Uuid

interface DailyQuestionRepository {
    suspend fun getOrCreateTodayQuestion(userId: Uuid): RepositoryResult<DailyQuestion>
    suspend fun submitAnswer(userId: Uuid, request: SubmitDailyQuestionAnswerRequest): RepositoryResult<DailyQuestionAnswerResult>
}
