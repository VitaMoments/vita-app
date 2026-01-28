package eu.vitamoments.app.data.mapper.extension_functions

import eu.vitamoments.app.data.repository.RepositoryResponse

inline fun <T, R> RepositoryResponse<T>.map(transform: (T) -> R): RepositoryResponse<R> {
    return when (this) {
        is RepositoryResponse.Success -> RepositoryResponse.Success(transform(body))
        is RepositoryResponse.Error -> this
    }
}

inline fun <T, R> RepositoryResponse<T>.flatMap(transform: (T) -> RepositoryResponse<R>): RepositoryResponse<R> {
    return when (this) {
        is RepositoryResponse.Success -> transform(body)
        is RepositoryResponse.Error -> this
    }
}
