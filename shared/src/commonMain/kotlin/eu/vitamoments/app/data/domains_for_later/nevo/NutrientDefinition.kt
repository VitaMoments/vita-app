package eu.vitamoments.app.data.domains_for_later.nevo

import kotlin.uuid.Uuid

data class NutrientDefinition(
    val uuid: Uuid,
    val code: String,
    val unit: UnitOfMeasure
)
