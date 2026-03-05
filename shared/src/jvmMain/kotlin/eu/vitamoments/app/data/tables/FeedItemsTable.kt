package eu.vitamoments.app.data.tables


import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.tables.base.Timestamps
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime


object FeedItemsTable : UUIDTable("feed_items"), Timestamps  {
    val type = enumerationByName<FeedItemType>(
        name = "type",
        length = 30
    )

    val author = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    )

    val privacy = enumerationByName<PrivacyStatus>(
        name = "privacy",
        length = 15
    ).clientDefault { PrivacyStatus.FRIENDS_ONLY }

    override val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    override val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    override val deletedAt = datetime("deleted_at").nullable()

    init {
        index(true, id)
        index(false, type)
        index(false, createdAt)
        index(false, updatedAt)
    }
}