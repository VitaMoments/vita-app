package eu.vitamoments.app.data.tables

import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.time
import org.jetbrains.exposed.v1.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object DailyQuestionAnswersTable : UUIDTable("daily_question_answers") {
    val questionItemId = reference("question_item_id", DailyQuestionItemsTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val questionIdSnapshot = uuid("question_id_snapshot")
    val questionTextSnapshot = varchar("question_text_snapshot", 500)
    val questionTypeSnapshot = varchar("question_type_snapshot", 30)
    val questionMinTimeSnapshot = time("question_min_time_snapshot").nullable()
    val questionMaxTimeSnapshot = time("question_max_time_snapshot").nullable()
    val questionAnswersSnapshot = json("question_answers_snapshot", Json, ListSerializer(String.serializer())).nullable()
    val answerText = text("answer_text").nullable()
    val selectedAnswer = varchar("selected_answer", 255).nullable()
    val answerDocument = json("answer_document", Json, JsonElement.serializer()).nullable()
    val answeredAt = datetime("answered_at")
    val answerDate = date("answer_date")

    init {
        index(true, questionItemId, userId)
        index(false, answerDate)
    }
}
