package eu.vitamoments.app.data.tables.nevo

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object ProductsTable : UUIDTable("products") {
    val nevoCode = integer("nevo_code").uniqueIndex()
    val nevoVersion = varchar("nevo_version", 64)

    val foodGroupId = reference("food_group_id", FoodGroupsTable, onDelete = ReferenceOption.RESTRICT)

    val nameNl = varchar("name_nl", 512)
    val nameEn = varchar("name_en", 512).nullable()
    val synonym = text("synonym").nullable()
    val quantity = varchar("quantity", 64).nullable()
    val remark = text("remark").nullable()
    val containsTracesOf = text("contains_traces_of").nullable()
    val fortifiedWith = text("fortified_with").nullable()
}