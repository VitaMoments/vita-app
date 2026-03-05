package eu.vitamoments.app.data.tables.base

import org.jetbrains.exposed.v1.core.Column
import kotlin.uuid.Uuid

interface Auditable {
    val createdBy: Column<Uuid?>
    val updatedBy: Column<Uuid?>
    val deletedBy: Column<Uuid?>
}