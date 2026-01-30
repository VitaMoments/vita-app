package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.api.ApiError
import eu.vitamoments.app.data.repository.RepositoryError
import io.ktor.http.HttpStatusCode

fun ApiError.toRepositoryError(status: HttpStatusCode): RepositoryError {
    val fields = fieldErrors.map { RepositoryError.FieldError(it.field, it.message) }

    return when (status) {
        HttpStatusCode.TooManyRequests ->
            RepositoryError.RequestLimitReached(message = message, code = code, traceId = traceId)

        HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden ->
            RepositoryError.Unauthorized(message = message, code = code, traceId = traceId)

        HttpStatusCode.NotFound ->
            RepositoryError.NotFound(message = message, code = code, traceId = traceId)

        HttpStatusCode.BadRequest ->
            RepositoryError.Validation(errors = fields, message = message, code = code, traceId = traceId)

        HttpStatusCode.Conflict ->
            RepositoryError.Conflict(errors = fields, message = message, code = code, traceId = traceId)

        else ->
            RepositoryError.Internal(message = message, code = code, traceId = traceId)
    }
}
