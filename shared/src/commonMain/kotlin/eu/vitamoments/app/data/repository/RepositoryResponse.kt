package eu.vitamoments.app.data.repository

sealed class RepositoryResponse<out T> {
    data class Success<T>(val body: T) : RepositoryResponse<T>()

    sealed class Error(
        open val message: String,
        open val code: String,
        open val traceId: String? = null      // optional correlation id (ApiError.traceId)
    ) : RepositoryResponse<Nothing>() {

        /**
         * One field-level error (validation / conflict / etc).
         */
        data class FieldError(
            val field: String,
            val message: String
        )

        /**
         * Helper to build a readable message from a list of field errors.
         */
        companion object {
            fun formatFieldErrors(errors: List<FieldError>): String =
                errors.joinToString(separator = "; ") { "${it.field}: ${it.message}" }
        }

        /**
         * 429 Too Many Requests (rate limiting)
         */
        data class RequestLimitReached(
            override val message: String = "Too many requests",
            override val code: String = "RATE_LIMIT",
            override val traceId: String? = null
        ) : Error(message, code, traceId)

        /**
         * 401/403
         */
        data class Unauthorized(
            override val message: String = "Unauthorized",
            override val code: String = "UNAUTHORIZED",
            override val traceId: String? = null
        ) : Error(message, code, traceId)

        /**
         * 404
         */
        data class NotFound(
            override val message: String = "Not found",
            override val code: String = "NOT_FOUND",
            override val traceId: String? = null
        ) : Error(message, code, traceId)

        /**
         * 400 validation errors (multiple fields).
         *
         * UI can show errors per field, or show message summary.
         */
        data class Validation(
            val errors: List<FieldError>,
            override val message: String = if (errors.isEmpty()) {
                "Invalid data"
            } else {
                "Invalid data: " + formatFieldErrors(errors)
            },
            override val code: String = "VALIDATION_ERROR",
            override val traceId: String? = null
        ) : Error(message, code, traceId)

        /**
         * 409 conflict errors (can also be multiple fields).
         *
         * Example: email already exists, slug already exists, etc.
         */
        data class Conflict(
            val errors: List<FieldError> = emptyList(),
            override val message: String = if (errors.isEmpty()) {
                "Conflict"
            } else {
                "Conflict: " + formatFieldErrors(errors)
            },
            override val code: String = "CONFLICT",
            override val traceId: String? = null
        ) : Error(message, code, traceId)

        /**
         * Anything unexpected.
         */
        data class Internal(
            override val message: String = "Something went wrong on our server. Please try again later",
            override val code: String = "INTERNAL",
            override val traceId: String? = null
        ) : Error(message, code, traceId)
    }
}