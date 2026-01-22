package eu.vitamoments.app.data.tables.nevo

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object ProductNutrientsTable : UUIDTable("product_nutrients") {
    val productId = reference("product_id", ProductsTable, onDelete = ReferenceOption.CASCADE)
    val nutrientId = reference("nutrient_id", NutrientsTable, onDelete = ReferenceOption.RESTRICT)
    val valueScaled = long("value_scaled").nullable()

    init {
        uniqueIndex(productId, nutrientId)
        index(false, nutrientId)
    }
}
