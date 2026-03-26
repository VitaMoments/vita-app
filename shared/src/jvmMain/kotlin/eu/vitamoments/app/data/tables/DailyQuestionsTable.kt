package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.QuestionType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.time
import org.jetbrains.exposed.v1.json.json

object DailyQuestionsTable : UUIDTable("daily_questions") {
    val question = varchar("question", 500)
    val type = enumerationByName<QuestionType>("type", 30)
    val minTime = time("min_time").nullable()
    val maxTime = time("max_time").nullable()
    val answers = json("answers", Json, ListSerializer(String.serializer())).nullable()
    val minDaysBetween = integer("min_days_between").nullable()
    val categories = json("categories", Json, ListSerializer(String.serializer())).nullable()

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val deletedAt = datetime("deleted_at").nullable()
}
