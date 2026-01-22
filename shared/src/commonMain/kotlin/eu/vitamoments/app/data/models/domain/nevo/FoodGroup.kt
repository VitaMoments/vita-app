package eu.vitamoments.app.data.models.domain.nevo

import kotlin.uuid.Uuid

data class FoodGroup(
    val uuid: Uuid,
    val groupNo: Int,
    val nameNl: String,
    val nameEn: String?
)
