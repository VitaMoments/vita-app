package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.DailyQuestionAnswersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class DailyQuestionAnswerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DailyQuestionAnswerEntity>(DailyQuestionAnswersTable)

    var questionItem by DailyQuestionItemEntity referencedOn DailyQuestionAnswersTable.questionItemId
    var user by UserEntity referencedOn DailyQuestionAnswersTable.userId
    var questionIdSnapshot by DailyQuestionAnswersTable.questionIdSnapshot
    var questionTextSnapshot by DailyQuestionAnswersTable.questionTextSnapshot
    var questionTypeSnapshot by DailyQuestionAnswersTable.questionTypeSnapshot
    var questionMinTimeSnapshot by DailyQuestionAnswersTable.questionMinTimeSnapshot
    var questionMaxTimeSnapshot by DailyQuestionAnswersTable.questionMaxTimeSnapshot
    var questionAnswersSnapshot by DailyQuestionAnswersTable.questionAnswersSnapshot
    var answerText by DailyQuestionAnswersTable.answerText
    var selectedAnswer by DailyQuestionAnswersTable.selectedAnswer
    var answerDocument by DailyQuestionAnswersTable.answerDocument
    var answeredAt by DailyQuestionAnswersTable.answeredAt
    var answerDate by DailyQuestionAnswersTable.answerDate
}
