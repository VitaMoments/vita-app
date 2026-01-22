package eu.vitamoments.app.data.mapper.extension_functions

import eu.vitamoments.app.data.models.domain.nevo.NutrientValue

fun NutrientValue.format(decimals: Int = nutrient.unit.defaultDecimals()): String? {
    val v = value ?: return null
    val txt = v.roundNevo(decimals).toNevoString()
    return "$txt ${nutrient.unit.raw}"
}