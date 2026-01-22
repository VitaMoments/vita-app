package eu.vitamoments.app.data.models.domain.nevo

import kotlin.uuid.Uuid

data class NutrientDefinition(
    val uuid: Uuid,
    val code: String,
    val unit: UnitOfMeasure
)
