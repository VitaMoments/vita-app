package eu.vitamoments.app.data.tables.nevo

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable


object NutrientsTable : UUIDTable("nutrients") {
    val code = varchar("code", 32).uniqueIndex()
    val unit = varchar("unit", 16)
}