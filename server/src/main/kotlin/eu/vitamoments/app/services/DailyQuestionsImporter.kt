package eu.vitamoments.app.services

import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.enums.QuestionType
import eu.vitamoments.app.data.tables.DailyQuestionsTable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

fun importDailyQuestionsFromJson(
    jsonPath: Path,
    overwriteExisting: Boolean = true
) {
    require(Files.exists(jsonPath)) { "Daily questions JSON not found: $jsonPath" }

    Files.newInputStream(jsonPath).use { inputStream ->
        importDailyQuestionsFromStream(inputStream, overwriteExisting)
    }
}

fun importDailyQuestionsFromStream(
    inputStream: InputStream,
    overwriteExisting: Boolean = true
) {
    val root = inputStream.bufferedReader().use { reader ->
        Json { ignoreUnknownKeys = true }
            .parseToJsonElement(reader.readText())
            .jsonObject
    }

    val questions = root["questions"]?.jsonArray
        ?: error("Daily questions JSON must contain a 'questions' array")

    transaction {
        questions.forEachIndexed { index, rawQuestion ->
            val item = rawQuestion.jsonObject

            val id = parseImportId(item.require("id", index))
            val questionText = item.requireString("question", index)
            val mappedType = parseQuestionType(item.requireString("type", index), id)

            val minTime = item.optionalString("minTime")?.let {
                runCatching { LocalTime.parse(it) }
                    .getOrElse { error("Invalid minTime '$it' for question $id") }
            }

            val maxTime = item.optionalString("maxTime")?.let {
                runCatching { LocalTime.parse(it) }
                    .getOrElse { error("Invalid maxTime '$it' for question $id") }
            }

            val answers = item.optionalStringArray("answers", id)

            val existing = DailyQuestionsTable
                .selectAll()
                .where { DailyQuestionsTable.id eq id }
                .limit(1)
                .firstOrNull()

            if (existing == null) {
                DailyQuestionsTable.insert {
                    it[DailyQuestionsTable.id] = id
                    it[question] = questionText
                    it[type] = mappedType
                    it[DailyQuestionsTable.minTime] = minTime
                    it[DailyQuestionsTable.maxTime] = maxTime
                    it[DailyQuestionsTable.answers] = answers
                    it[createdAt] = LocalDateTime.nowUtc()
                    it[updatedAt] = LocalDateTime.nowUtc()
                    it[deletedAt] = null
                }
            } else if (overwriteExisting) {
                DailyQuestionsTable.update({ DailyQuestionsTable.id eq id }) {
                    it[question] = questionText
                    it[type] = mappedType
                    it[DailyQuestionsTable.minTime] = minTime
                    it[DailyQuestionsTable.maxTime] = maxTime
                    it[DailyQuestionsTable.answers] = answers
                    it[updatedAt] = LocalDateTime.nowUtc()
                }
            }
        }
    }
}

private fun parseImportId(raw: JsonElement): UUID {
    val primitive = raw as? JsonPrimitive
        ?: error("Daily question id must be a JSON primitive")

    primitive.longOrNull?.let { numericId ->
        return UUID.nameUUIDFromBytes("daily-question-$numericId".toByteArray())
    }

    val text = primitive.content.trim()
    runCatching { UUID.fromString(text) }.getOrNull()?.let { return it }

    text.toLongOrNull()?.let { numericId ->
        return UUID.nameUUIDFromBytes("daily-question-$numericId".toByteArray())
    }

    error("Daily question id must be UUID or numeric, got '$text'")
}

private fun parseQuestionType(raw: String, id: UUID): QuestionType = when (raw.lowercase()) {
    "open" -> QuestionType.OPEN
    "multiple_choice" -> QuestionType.MULTIPLE_CHOICE
    else -> error("Invalid question type '$raw' for question $id")
}

private fun JsonObject.require(field: String, index: Int): JsonElement =
    this[field] ?: error("Missing required field '$field' for question at index $index")

private fun JsonObject.requireString(field: String, index: Int): String =
    this[field]?.jsonPrimitive?.contentOrNull
        ?: error("Missing or invalid string field '$field' for question at index $index")

private fun JsonObject.optionalString(field: String): String? =
    this[field]?.jsonPrimitive?.contentOrNull

private fun JsonObject.optionalStringArray(field: String, id: UUID): List<String>? {
    val value = this[field] ?: return null
    return when (value) {
        is JsonArray -> value.mapIndexed { idx, element ->
            element.jsonPrimitive.contentOrNull
                ?: error("Invalid answer at index $idx for question $id")
        }
        else -> null
    }
}


