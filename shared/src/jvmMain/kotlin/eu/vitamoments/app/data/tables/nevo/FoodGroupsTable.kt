package eu.vitamoments.app.data.tables.nevo

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object FoodGroupsTable : UUIDTable("food_groups") {
    val groupNo = integer("group_no").uniqueIndex()
    val nameNl = varchar("name_nl", 255).uniqueIndex()
    val nameEn = varchar("name_en",255).nullable().uniqueIndex()

}