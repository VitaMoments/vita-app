package eu.vitamoments.app.data.domains_for_later.nevo

import kotlin.uuid.Uuid

data class FoodGroup(
    val uuid: Uuid,
    val groupNo: Int,
    val nameNl: String,
    val nameEn: String?
)
