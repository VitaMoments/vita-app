package eu.vitamoments.app.data.repository

sealed class RepositoryResponse<out T> {
    data class Success<T>(val body: T) : RepositoryResponse<T>()

    sealed class Error(open val message: String) : RepositoryResponse<Nothing>() {
        data class RequestLimitReached(override val message: String = "Too many requests"): Error(message)

        data class Conflict(val key: String, override val message: String) : Error(message)

        data class InvalidData(val key: String, override val message: String) : Error(message)

        data class Validation(
            val errors: List<InvalidData>,
            override val message: String = "Invalid data: " + errors.joinToString(separator = "; ") { "${it.key}: ${it.message}" }
        ) : Error(message)
        data class Unauthorized(override val message: String) : Error(message)
        data class NotFound(override val message: String) : Error(message)
        data class Internal(override val message: String = "Something went wrong on our server. Please try again later") : Error(message)
    }
}