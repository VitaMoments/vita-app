package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class BlogCategory {
    // Core pillars
    MENTAL,
    PHYSICAL,
    FOOD,
    LIFESTYLE,

    // Mind & habits
    MINDFULNESS,
    HABITS,
    SLEEP,
    ENERGY,

    // Social & meaning
    RELATIONSHIPS,
    COMMUNITY,
    PURPOSE,

    // Reflection & growth
    PERSONAL_GROWTH,
    REFLECTION
}

