package eu.vitamoments.app.data.tables.base

import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime


// todo: change base id to uuid
abstract class BaseUUIDTable(name: String) : UUIDTable(name = name), Auditable, Timestamps {
    override val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    override val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    override val deletedAt = datetime("deleted_at").nullable()

    override val createdBy = uuid("created_by").nullable()
    override val updatedBy = uuid("updated_by").nullable()
    override val deletedBy = uuid("deleted_by").nullable()
}