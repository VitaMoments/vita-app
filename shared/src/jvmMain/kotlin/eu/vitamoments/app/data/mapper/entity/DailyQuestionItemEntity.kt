package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.DailyQuestionItemEntity
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.DailyQuestionItem
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.dbHelpers.kotlinUuid
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun DailyQuestionItemEntity.toDomain(viewerUuid: Uuid): DailyQuestionItem {
    val rawCategories = this.question.categories ?: emptyList()
    val categories = rawCategories.mapNotNull { name ->
        runCatching { enumValueOf<FeedCategory>(name) }.getOrNull()
    }

    return DailyQuestionItem(
        uuid = this.kotlinUuid,
        questionId = this.question.id.value.toKotlinUuid(),
        question = this.question.question,
        questionType = this.question.type,
        categories = categories,
        minTime = this.question.minTime?.toString(),
        maxTime = this.question.maxTime?.toString(),
        answers = this.question.answers,
        questionDate = this.questionDate.toString(),
        author = this.feedItem.author.toDomainForViewer(viewerUuid),
        content = RichTextDocument(type = "daily-question", content = null),
        privacy = this.feedItem.privacy,
        createdAt = this.feedItem.createdAt.toInstant(),
        updatedAt = this.feedItem.updatedAt.toInstant(),
        deletedAt = this.feedItem.deletedAt?.toInstant(),
    )
}
