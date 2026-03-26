package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.DailyQuestionItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class DailyQuestionItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DailyQuestionItemEntity>(DailyQuestionItemsTable)

    var feedItem by FeedItemEntity referencedOn DailyQuestionItemsTable.feedItemId
    var question by DailyQuestionEntity referencedOn DailyQuestionItemsTable.questionId
    var questionDate by DailyQuestionItemsTable.questionDate
}

