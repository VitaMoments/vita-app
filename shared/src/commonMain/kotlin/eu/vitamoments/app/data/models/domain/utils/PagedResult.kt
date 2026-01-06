package eu.vitamoments.app.data.models.domain.utils

data class PagedResult<T>(
    val items: List<T>,
    val limit: Int,
    val offset: Long,
    val total: Long,
    val hasMore: Boolean,
    val nextOffset: Long?
)
