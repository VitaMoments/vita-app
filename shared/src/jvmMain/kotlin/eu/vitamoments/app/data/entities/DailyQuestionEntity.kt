package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.DailyQuestionsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class DailyQuestionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DailyQuestionEntity>(DailyQuestionsTable)

    var question by DailyQuestionsTable.question
    var type by DailyQuestionsTable.type
    var minTime by DailyQuestionsTable.minTime
    var maxTime by DailyQuestionsTable.maxTime
    var answers by DailyQuestionsTable.answers
    var minDaysBetween by DailyQuestionsTable.minDaysBetween
    var categories by DailyQuestionsTable.categories
    var createdAt by DailyQuestionsTable.createdAt
    var updatedAt by DailyQuestionsTable.updatedAt
    var deletedAt by DailyQuestionsTable.deletedAt
}
