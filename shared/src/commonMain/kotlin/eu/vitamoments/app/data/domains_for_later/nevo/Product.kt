package eu.vitamoments.app.data.domains_for_later.nevo

import kotlin.uuid.Uuid

data class Product(
    val uuid: Uuid,
    val nevoCode: Int,
    val nevoVersion: String,
    val foodGroup: FoodGroup,
    val nameNl: String,
    val nameEn: String?,
    val synonym: String?,
    val quantity: String?,
    val remark: String?,
    val containsTracesOf: String?,
    val fortifiedWith: String?,
    val nutrients: List<NutrientValue>
) {
    fun nutrientMap(): Map<String, Long?> = buildMap {
        nutrients.forEach { nv ->
            require(put(nv.nutrient.code, nv.value) == null) {
                "Duplicate nutrient code: ${nv.nutrient.code}"
            }
        }
    }

    fun valueOf(code: String): Long? =
        nutrients.firstOrNull { it.nutrient.code == code }?.value
}
