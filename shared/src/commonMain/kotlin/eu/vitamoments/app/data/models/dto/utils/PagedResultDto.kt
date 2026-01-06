package eu.vitamoments.app.data.models.dto.utils

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class PagedResultDto<T>(
    val items: List<@Polymorphic T>,
    val limit: Int,
    val offset: Long,
    val total: Long,
    val hasMore: Boolean,
    val nextOffset: Long?
)
