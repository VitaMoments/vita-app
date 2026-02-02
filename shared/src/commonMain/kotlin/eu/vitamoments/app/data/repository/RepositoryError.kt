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
        val errors: List<FieldError>,
        override val message: String = if (errors.isEmpty()) "Invalid data" else
            "Invalid data: " + errors.joinToString("; ") { "${it.field}: ${it.message}" },
        override val code: ErrorCode = ErrorCode.VALIDATION_ERROR,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class Conflict(
        val errors: List<FieldError> = emptyList(),
        override val message: String = if (errors.isEmpty()) "Conflict" else
            "Conflict: " + errors.joinToString("; ") { "${it.field}: ${it.message}" },
        override val code: ErrorCode = ErrorCode.CONFLICT,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class BadRequest(
        val errors: List<FieldError> = emptyList(),
        override val message: String = if (errors.isEmpty()) "Bad_Request" else
            "Bad Request: " + errors.joinToString("; ") { "${it.field}: ${it.message}" },
        override val code: ErrorCode = ErrorCode.BAD_REQUEST,
        override val traceId: String? = null
    ) : RepositoryError(message, code, traceId)

    data class Internal(
        override val message: String = "Something went wrong on our server. Please try again later",
        override val code: ErrorCode = ErrorCode.INTERNAL,
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

    }

    fun toApiError(): ApiError {
        val fieldErrors = when (this) {
            is Validation -> errors
            is Conflict -> errors
            else -> emptyList()
        }.map { ApiFieldError(field = it.field, message = it.message) }

        return ApiError(
            code = code,
            message = message,
            fieldErrors = fieldErrors,
            traceId = traceId
        )
    }
}
