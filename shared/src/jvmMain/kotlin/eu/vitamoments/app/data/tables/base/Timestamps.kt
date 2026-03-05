package eu.vitamoments.app.data.tables.base

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column

interface Timestamps {
    val createdAt: Column<LocalDateTime>
    val updatedAt: Column<LocalDateTime>
    val deletedAt: Column<LocalDateTime?>
}