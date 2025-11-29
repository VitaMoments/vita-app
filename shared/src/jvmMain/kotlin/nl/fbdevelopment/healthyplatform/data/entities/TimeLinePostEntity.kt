package nl.fbdevelopment.healthyplatform.data.entities

import nl.fbdevelopment.healthyplatform.data.tables.TimeLinePostsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class TimeLinePostEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TimeLinePostEntity>(TimeLinePostsTable)
    var createdBy by UserEntity referencedOn TimeLinePostsTable.createdBy
    var createdAt by TimeLinePostsTable.createdAt
    var updatedAt by TimeLinePostsTable.updatedAt
    var deletedAt by TimeLinePostsTable.deletedAt
    var content by TimeLinePostsTable.content
}