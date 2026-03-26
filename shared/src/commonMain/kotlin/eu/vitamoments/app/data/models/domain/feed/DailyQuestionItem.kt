package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.enums.QuestionType
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("DAILYQUESTIONITEM")
data class DailyQuestionItem(
    override val uuid: Uuid,
    val questionItemId: Uuid,
    val questionId: Uuid,
    val question: String,
    val questionType: QuestionType,
    val categories: List<FeedCategory> = emptyList(),
    val selectedAnswer: String? = null,
    val answerText: String? = null,
    override val author: User,
    override val content: RichTextDocument,
    override val privacy: PrivacyStatus = PrivacyStatus.PRIVATE,
    @Serializable(with = InstantSerializer::class) override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) override val updatedAt: Instant,
    @Serializable(with = InstantSerializer::class) override val deletedAt: Instant? = null,
) : FeedItem
