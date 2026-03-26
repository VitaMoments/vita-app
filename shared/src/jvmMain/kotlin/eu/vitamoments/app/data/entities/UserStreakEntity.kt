package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.UserStreaksTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class UserStreakEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserStreakEntity>(UserStreaksTable)

    var user by UserEntity referencedOn UserStreaksTable.userId
    var userId by UserStreaksTable.userId
    var currentStreak by UserStreaksTable.currentStreak
    var longestStreak by UserStreaksTable.longestStreak
    var lastAnsweredDate by UserStreaksTable.lastAnsweredDate
    var updatedAt by UserStreaksTable.updatedAt
}

