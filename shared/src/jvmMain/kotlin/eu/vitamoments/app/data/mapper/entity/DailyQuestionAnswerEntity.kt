package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.DailyQuestionAnswerEntity
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.DailyQuestionItem
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.enums.QuestionType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun DailyQuestionAnswerEntity.toFeedDomain(
    viewerUuid: Uuid,
    friendshipStatusProvider: (authorUuid: Uuid) -> eu.vitamoments.app.data.models.enums.FriendshipStatus? = { null }
): DailyQuestionItem {
    val authorUuid = this.user.id.value.toKotlinUuid()
    val friendship = friendshipStatusProvider(authorUuid)

    val rawCategories = this.questionItem.question.categories ?: emptyList()
    val categories = rawCategories.mapNotNull { name ->
        runCatching { enumValueOf<FeedCategory>(name) }.getOrNull()
    }

    val content = when {
        this.answerDocument != null -> RichTextDocument(content = this.answerDocument)
        !this.answerText.isNullOrBlank() -> RichTextDocument(
            content = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("doc"),
                    "content" to JsonArray(
                        listOf(
                            JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("paragraph"),
                                    "content" to JsonArray(
                                        listOf(
                                            JsonObject(
                                                mapOf(
                                                    "type" to JsonPrimitive("text"),
                                                    "text" to JsonPrimitive(this.answerText)
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        !this.selectedAnswer.isNullOrBlank() -> RichTextDocument(
            content = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("doc"),
                    "content" to JsonArray(
                        listOf(
                            JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("paragraph"),
                                    "content" to JsonArray(
                                        listOf(
                                            JsonObject(
                                                mapOf(
                                                    "type" to JsonPrimitive("text"),
                                                    "text" to JsonPrimitive(this.selectedAnswer)
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        else -> RichTextDocument(content = null)
    }

    return DailyQuestionItem(
        uuid = this.id.value.toKotlinUuid(),
        questionItemId = this.questionItem.id.value.toKotlinUuid(),
        questionId = this.questionIdSnapshot,
        question = this.questionTextSnapshot,
        questionType = runCatching { enumValueOf<QuestionType>(this.questionTypeSnapshot) }
            .getOrDefault(QuestionType.OPEN),
        categories = categories,
        selectedAnswer = this.selectedAnswer,
        answerText = this.answerText,
        author = this.user.toDomainForViewer(viewerUuid, friendship),
        content = content,
        privacy = PrivacyStatus.PRIVATE,
        createdAt = this.answeredAt.toInstant(),
        updatedAt = this.answeredAt.toInstant(),
        deletedAt = null,
    )
}

