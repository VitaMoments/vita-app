package eu.vitamoments.app.dbHelpers

import org.jetbrains.exposed.v1.dao.Entity
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

val Entity<UUID>.kotinUuid : Uuid get() = this.id.value.toKotlinUuid()