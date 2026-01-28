package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.api.ApiError
import eu.vitamoments.app.data.repository.RepositoryResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException

/**
 * Maps a HttpResponse into RepositoryResponse<T>.
 *
 * - Success (200/201) -> parse body as T
 * - NoContent (204)   -> for Unit only, use toEmptyRepositoryResponse()
 * - Error status      -> try parse ApiError, fallback to status-based messages
 */
suspend inline fun <reified T> HttpResponse.toRepositoryResponse(): RepositoryResponse<T> {
    return try {
        when (status) {
            HttpStatusCode.OK,
            HttpStatusCode.Created -> RepositoryResponse.Success(body())

            HttpStatusCode.NoContent -> RepositoryResponse.Error.Internal("No content")

            else -> mapErrorResponse()
        }
    } catch (e: SerializationException) {
        RepositoryResponse.Error.Internal("Serialization error: ${e.message}")
    } catch (t: Throwable) {
        RepositoryResponse.Error.Internal("Unknown error: ${t.message}")
    }
}

/**
 * Variant for endpoints that return no response body (or you don't care).
 */
suspend fun HttpResponse.toEmptyRepositoryResponse(): RepositoryResponse<Unit> {
    return try {
        when (status) {
            HttpStatusCode.OK,
            HttpStatusCode.Created,
            HttpStatusCode.NoContent -> RepositoryResponse.Success(Unit)

            else -> mapErrorResponse()
        }
    } catch (t: Throwable) {
        RepositoryResponse.Error.Internal("Unknown error: ${t.message}")
    }
}

/**
 * Converts an error HttpResponse into a RepositoryResponse.Error.
 *
 * Behavior:
 * - First tries to parse ApiError { code, message, fieldErrors, traceId }
 * - If parsing fails (plain text/html), falls back to status-based messages
 * - Preserves multiple field errors for Validation/Conflict
 */
suspend fun HttpResponse.mapErrorResponse(): RepositoryResponse.Error {
    val apiError: ApiError? = runCatching { body<ApiError>() }.getOrNull()

    val fieldErrors: List<RepositoryResponse.Error.FieldError> =
        apiError?.fieldErrors
            ?.map { RepositoryResponse.Error.FieldError(field = it.field, message = it.message) }
            .orEmpty()

    val code = apiError?.code
    val traceId = apiError?.traceId
    val msg = apiError?.message

    return when (status) {
        HttpStatusCode.BadRequest -> {
            RepositoryResponse.Error.Validation(
                errors = fieldErrors,
                message = msg ?: if (fieldErrors.isEmpty()) "Invalid data" else "Invalid data: ${RepositoryResponse.Error.formatFieldErrors(fieldErrors)}",
                code = code ?: "VALIDATION_ERROR",
                traceId = traceId
            )
        }

        HttpStatusCode.Conflict -> {
            RepositoryResponse.Error.Conflict(
                errors = fieldErrors,
                message = msg ?: if (fieldErrors.isEmpty()) "Conflict" else "Conflict: ${RepositoryResponse.Error.formatFieldErrors(fieldErrors)}",
                code = code ?: "CONFLICT",
                traceId = traceId
            )
        }

        HttpStatusCode.TooManyRequests -> {
            RepositoryResponse.Error.RequestLimitReached(
                message = msg ?: "Too many requests",
                code = code ?: "RATE_LIMIT",
                traceId = traceId
            )
        }

        HttpStatusCode.Unauthorized,
        HttpStatusCode.Forbidden -> {
            RepositoryResponse.Error.Unauthorized(
                message = msg ?: "Unauthorized",
                code = code ?: "UNAUTHORIZED",
                traceId = traceId
            )
        }

        HttpStatusCode.NotFound -> {
            RepositoryResponse.Error.NotFound(
                message = msg ?: "Not found",
                code = code ?: "NOT_FOUND",
                traceId = traceId
            )
        }

        else -> {
            RepositoryResponse.Error.Internal(
                message = msg ?: "Unexpected status: ${status.value} ${status.description}",
                code = code ?: "INTERNAL",
                traceId = traceId
            )
        }
    }
}
