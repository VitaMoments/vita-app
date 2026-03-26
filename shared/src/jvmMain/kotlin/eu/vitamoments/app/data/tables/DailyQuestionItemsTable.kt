package eu.vitamoments.app.data.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.date

object DailyQuestionItemsTable : UUIDTable("daily_question_items") {
    val feedItemId = reference("feed_item_id", FeedItemsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val questionId = reference("question_id", DailyQuestionsTable, onDelete = ReferenceOption.RESTRICT)
    val questionDate = date("question_date")

    init {
        // unique per (question, date): same question appears at most once per day globally
        index(true, questionId, questionDate)
    }
}
