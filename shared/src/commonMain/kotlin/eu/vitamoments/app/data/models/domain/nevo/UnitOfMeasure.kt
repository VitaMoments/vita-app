package eu.vitamoments.app.data.models.domain.nevo

sealed interface UnitOfMeasure {
    val raw: String
    enum class Known(override val raw: String): UnitOfMeasure {
        G("g"),
        MG("mg"),
        UG("µg"),
        KJ("kJ"),
        KCAL("kcal")
    }

    data class Unknown(override val raw: String) : UnitOfMeasure
}
fun parseUnit(raw: String): UnitOfMeasure = UnitOfMeasure.Known.entries.firstOrNull{ it.raw == raw } ?: UnitOfMeasure.Unknown(raw)