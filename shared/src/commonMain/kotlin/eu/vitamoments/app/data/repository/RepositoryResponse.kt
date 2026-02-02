package eu.vitamoments.app.data.repository

sealed class RepositoryResult<out T> {
    data class Success<T>(val body: T) : RepositoryResult<T>()
    data class Error(val error: RepositoryError) : RepositoryResult<Nothing>()
}