package eu.vitamoments.app.data.mapper.extension_functions

import eu.vitamoments.app.data.models.domain.nevo.Product

fun Product.nutrientMapUnsafeOverwrite(): Map<String, Long?> =
    nutrients.associate { it.nutrient.code to it.value }

// behoud jouw “duplicate check” versie als default:
fun Product.nutrientMapChecked(): Map<String, Long?> = buildMap {
    nutrients.forEach { nv ->
        require(put(nv.nutrient.code, nv.value) == null) {
            "Duplicate nutrient code: ${nv.nutrient.code}"
        }
    }
}

fun Product.formattedValueOf(code: String, decimals: Int? = null): String? {
    val nv = nutrients.firstOrNull { it.nutrient.code == code } ?: return null
    val v = nv.value ?: return null
    val d = decimals ?: nv.nutrient.unit.defaultDecimals()
    return "${v.roundNevo(d).toNevoString()} ${nv.nutrient.unit.raw}"
}
