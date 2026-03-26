package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.DailyQuestionAnswerEntity
import eu.vitamoments.app.data.entities.DailyQuestionEntity
import eu.vitamoments.app.data.entities.DailyQuestionItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.entities.UserStreakEntity
import eu.vitamoments.app.data.factory.FeedItemFactory
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.daily.DailyQuestionAnswerResult
import eu.vitamoments.app.data.models.domain.feed.DailyQuestionItem
import eu.vitamoments.app.data.models.enums.QuestionType
import eu.vitamoments.app.data.models.requests.daily_questions_requests.SubmitDailyQuestionAnswerRequest
import eu.vitamoments.app.data.tables.DailyQuestionAnswersTable
import eu.vitamoments.app.data.tables.DailyQuestionItemsTable
import eu.vitamoments.app.data.tables.UserStreaksTable
import eu.vitamoments.app.dbHelpers.dbQuery
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import java.util.UUID
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class JVMDailyQuestionRepository : DailyQuestionRepository {

    /**
     * Returns the next available question for this user today.
     *
     * Rules:
     * - A user may answer multiple different questions per day.
     * - A user may NOT answer the same question twice on the same day.
     * - If minDaysBetween is set, the question may not have been answered
     *   within that many days (exclusive: minDaysBetween=1 means ≥1 full day gap).
     * - If no eligible question remains, returns NotFound with a specific message.
     */
    override suspend fun getOrCreateTodayQuestion(userId: Uuid): RepositoryResult<DailyQuestionItem> = dbQuery {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nowTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        val userIdJava = userId.toJavaUuid()

        // Question IDs the user already answered today (as java.util.UUID to match entity field)
        val answeredTodayQuestionIds: Set<Uuid> = DailyQuestionAnswerEntity
            .find {
                (DailyQuestionAnswersTable.userId eq userIdJava) and
                    (DailyQuestionAnswersTable.answerDate eq today)
            }
            .mapTo(mutableSetOf()) { it.questionIdSnapshot }

        // All active questions within the current time window
        val candidates = DailyQuestionEntity
            .all()
            .filter { it.deletedAt == null }
            .filter { isWithinWindow(nowTime, it.minTime, it.maxTime) }
            // Not answered today
            .filter { it.id.value.toKotlinUuid() !in answeredTodayQuestionIds }
            // minDaysBetween: find last answer for this specific question by this user
            .filter { question ->
                val minDays = question.minDaysBetween ?: return@filter true
                val questionIdSnapshot = question.id.value.toKotlinUuid()

                val lastAnswer = DailyQuestionAnswerEntity
                    .find {
                        (DailyQuestionAnswersTable.userId eq userIdJava) and
                            (DailyQuestionAnswersTable.questionIdSnapshot eq questionIdSnapshot)
                    }
                    .orderBy(DailyQuestionAnswersTable.answerDate to SortOrder.DESC)
                    .firstOrNull()
                    ?: return@filter true

                (today.toEpochDays() - lastAnswer.answerDate.toEpochDays()).toInt() >= minDays
            }
        if (candidates.isEmpty()) {
            return@dbQuery RepositoryResult.Error(RepositoryError.NotFound("no_more_questions_today"))
        }

        val author = UserEntity.all().firstOrNull()
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound("No users found to attach feed item author")
            )

        val question = candidates.random()

        // Reuse existing DailyQuestionItem for this (question, date) pair if present
        val item = DailyQuestionItemEntity
            .find {
                (DailyQuestionItemsTable.questionId eq question.id) and
                    (DailyQuestionItemsTable.questionDate eq today)
            }
            .firstOrNull()
            ?: FeedItemFactory.newDailyQuestionItem(
                author = author,
                question = question,
                questionDate = today
            )

        RepositoryResult.Success(item.toDomain(userId))
    }

    override suspend fun submitAnswer(
        userId: Uuid,
        request: SubmitDailyQuestionAnswerRequest
    ): RepositoryResult<DailyQuestionAnswerResult> = dbQuery {
        val questionItemIdJava = request.questionItemId.toJavaUuid()
        val userIdJava = userId.toJavaUuid()

        val questionItem = DailyQuestionItemEntity.findById(questionItemIdJava)
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound("Daily question item not found")
            )

        val user = UserEntity.findById(userIdJava)
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound("User not found")
            )

        val existingAnswer = DailyQuestionAnswerEntity.find {
            (DailyQuestionAnswersTable.questionItemId eq questionItemIdJava) and
                (DailyQuestionAnswersTable.userId eq userIdJava)
        }.firstOrNull()

        val streak = getOrCreateUserStreak(user)

        if (existingAnswer != null) {
            return@dbQuery RepositoryResult.Success(
                DailyQuestionAnswerResult(
                    accepted = false,
                    currentStreak = streak.currentStreak,
                    longestStreak = streak.longestStreak,
                    alreadyAnsweredToday = true,
                )
            )
        }

        val normalizedText = request.answerText?.trim()?.ifBlank { null }
        val normalizedSelected = request.selectedAnswer?.trim()?.ifBlank { null }

        when (questionItem.question.type) {
            QuestionType.OPEN -> {
                val hasText = normalizedText != null
                val hasDocument = request.answerDocument?.content != null
                if (!hasText && !hasDocument) {
                    return@dbQuery RepositoryResult.Error(
                        RepositoryError.BadRequest(
                            errors = listOf(
                                RepositoryError.FieldError("answerDocument", "Answer is required for open question")
                            )
                        )
                    )
                }
            }

            QuestionType.MULTIPLE_CHOICE -> {
                val allowedAnswers = questionItem.question.answers.orEmpty()
                if (normalizedSelected == null || normalizedSelected !in allowedAnswers) {
                    return@dbQuery RepositoryResult.Error(
                        RepositoryError.BadRequest(
                            errors = listOf(
                                RepositoryError.FieldError("selectedAnswer", "Selected answer is invalid")
                            )
                        )
                    )
                }
            }
        }

        val answeredAt = kotlinx.datetime.LocalDateTime.nowUtc()
        val answerDate = answeredAt.date

        DailyQuestionAnswerEntity.new(UUID.randomUUID()) {
            this.questionItem = questionItem
            this.user = user
            this.questionIdSnapshot = questionItem.question.id.value.toKotlinUuid()
            this.questionTextSnapshot = questionItem.question.question
            this.questionTypeSnapshot = questionItem.question.type.name
            this.questionMinTimeSnapshot = questionItem.question.minTime
            this.questionMaxTimeSnapshot = questionItem.question.maxTime
            this.questionAnswersSnapshot = questionItem.question.answers
            this.answerText = normalizedText
            this.selectedAnswer = normalizedSelected
            this.answerDocument = request.answerDocument?.content
            this.answeredAt = answeredAt
            this.answerDate = answerDate
        }

        val previousDate = streak.lastAnsweredDate
        val updatedStreak = when {
            previousDate == null -> 1
            previousDate == answerDate -> streak.currentStreak
            (answerDate.toEpochDays() - previousDate.toEpochDays()).toInt() == 1 -> streak.currentStreak + 1
            else -> 1
        }

        streak.currentStreak = updatedStreak
        streak.longestStreak = maxOf(streak.longestStreak, updatedStreak)
        streak.lastAnsweredDate = answerDate
        streak.updatedAt = answeredAt

        RepositoryResult.Success(
            DailyQuestionAnswerResult(
                accepted = true,
                currentStreak = streak.currentStreak,
                longestStreak = streak.longestStreak,
                alreadyAnsweredToday = false,
            )
        )
    }

    private fun getOrCreateUserStreak(user: UserEntity): UserStreakEntity {
        return UserStreakEntity.find { UserStreaksTable.userId eq user.id }.firstOrNull()
            ?: UserStreakEntity.new(UUID.randomUUID()) {
                this.user = user
                this.currentStreak = 0
                this.longestStreak = 0
                this.lastAnsweredDate = null
                this.updatedAt = kotlinx.datetime.LocalDateTime.nowUtc()
            }
    }

    private fun isWithinWindow(
        now: kotlinx.datetime.LocalTime,
        minTime: kotlinx.datetime.LocalTime?,
        maxTime: kotlinx.datetime.LocalTime?
    ): Boolean = when {
        minTime == null && maxTime == null -> true
        minTime == null -> now < maxTime!!
        maxTime == null -> now >= minTime
        else -> now in minTime..<maxTime
    }
}
