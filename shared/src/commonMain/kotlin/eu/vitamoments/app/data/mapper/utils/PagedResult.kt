package eu.vitamoments.app.data.mapper.utils

import eu.vitamoments.app.data.models.dto.utils.PagedResultDto
import eu.vitamoments.app.data.models.domain.utils.PagedResult

fun <T, D> PagedResult<T>.toDto(mapper: (T) -> D): PagedResultDto<D> =
    PagedResultDto(
        items = items.map(mapper),
        limit = limit,
        offset = offset,
        total = total,
        hasMore = hasMore,
        nextOffset = nextOffset
    )