package eu.vitamoments.app.data.mapper.extension_functions

import eu.vitamoments.app.data.models.domain.nevo.UnitOfMeasure

fun UnitOfMeasure.defaultDecimals(): Int = when (this) {
    is UnitOfMeasure.Known -> when (this) {
        UnitOfMeasure.Known.KJ, UnitOfMeasure.Known.KCAL -> 0
        UnitOfMeasure.Known.G -> 1
        UnitOfMeasure.Known.MG, UnitOfMeasure.Known.UG -> 0
    }
    is UnitOfMeasure.Unknown -> 2
}

fun parseUnit(raw: String): UnitOfMeasure =
    UnitOfMeasure.Known.entries.firstOrNull { it.raw == raw } ?: UnitOfMeasure.Unknown(raw)