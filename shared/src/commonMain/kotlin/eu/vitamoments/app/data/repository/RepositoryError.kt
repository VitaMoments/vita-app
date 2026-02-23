package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.api.ApiError
import eu.vitamoments.app.data.models.domain.api.ErrorCode
import eu.vitamoments.app.data.models.domain.api.ApiFieldError
import io.ktor.http.HttpStatusCode

sealed class RepositoryError(
    open val message: String,
    open val code: ErrorCode,
    open val traceId: String? = null
) {

    sealed interface HasFieldErrors {
        val errors: List<FieldError>
    }

    data class FieldError(val field: String, val message: String)

    data class RequestLimitReached(
        override val message: String = "Too many requests",
        override val code: ErrorCode = ErrorCode.RATE_LIMIT,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class Unauthorized(
        override val message: String = "Unauthorized",
        override val code: ErrorCode = ErrorCode.UNAUTHORIZED,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class NotFound(
        override val message: String = "Not found",
        override val code: ErrorCode = ErrorCode.NOT_FOUND,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class Validation(
        override val errors: List<FieldError>,
        override val message: String = "Invalid data",
        override val code: ErrorCode = ErrorCode.VALIDATION_ERROR,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId), HasFieldErrors

    data class Conflict(
        override val errors: List<FieldError> = emptyList(),
        override val message: String = "Invalid data",
        override val code: ErrorCode = ErrorCode.CONFLICT,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId), HasFieldErrors

    data class BadRequest(
        override val errors: List<FieldError> = emptyList(),
        override val message: String = if (errors.isEmpty()) "Bad_Request" else
            "Bad Request: " + errors.joinToString("; ") { "${it.field}: ${it.message}" },
        override val code: ErrorCode = ErrorCode.BAD_REQUEST,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId), HasFieldErrors

    data class Internal(
        override val message: String = "Something went wrong on our server. Please try again later",
        override val code: ErrorCode = ErrorCode.INTERNAL,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class Forbidden(
        override val message: String = "Forbidden",
        override val code: ErrorCode = ErrorCode.FORBIDDEN,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)


    fun statusCode(): HttpStatusCode = when (this) {
        is RequestLimitReached -> HttpStatusCode.TooManyRequests
        is Conflict -> HttpStatusCode.Conflict
        is Validation -> HttpStatusCode.BadRequest
        is Unauthorized -> HttpStatusCode.Unauthorized
        is NotFound -> HttpStatusCode.NotFound
        is BadRequest -> HttpStatusCode.BadRequest
        is Internal -> HttpStatusCode.InternalServerError
        is Forbidden -> HttpStatusCode.Forbidden
    }

    fun toApiError(): ApiError {
        val fieldErrors = (this as? HasFieldErrors)
            ?.errors
            ?.map { ApiFieldError(it.field, it.message) }
            ?: emptyList()

        return ApiError(
            code = code,
            message = message,
            fieldErrors = fieldErrors,
            traceId = traceId
        )
    }
}
