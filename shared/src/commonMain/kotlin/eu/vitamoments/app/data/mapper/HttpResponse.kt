package eu.vitamoments.app.data.mapper

import eu.vitamoments.app.data.models.domain.api.ApiError
import eu.vitamoments.app.data.models.domain.api.ErrorCode
import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.RepositoryResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> HttpResponse.toRepositoryResult(): RepositoryResult<T> {
    if (status.isSuccess()) {
        return if (status == HttpStatusCode.NoContent) {
            @Suppress("UNCHECKED_CAST")
            RepositoryResult.Success(Unit as T)
        } else {
            try {
                RepositoryResult.Success(body())
            } catch (e: SerializationException) {
                RepositoryResult.Error(
                    RepositoryError.Internal(
                        message = "Serialization error: ${e.message}",
                        code = ErrorCode.SERIALIZATION
                    )
                )
            }
        }
    }

    val apiError: ApiError? = runCatching { body<ApiError>() }.getOrNull()
    val repoError = when {
        apiError != null -> apiError.toRepositoryError(status)
        else -> status.toFallbackRepositoryError()
    }
    return RepositoryResult.Error(repoError)
}

fun HttpStatusCode.toFallbackRepositoryError(): RepositoryError = when (this) {
    HttpStatusCode.TooManyRequests -> RepositoryError.RequestLimitReached()
    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> RepositoryError.Unauthorized()
    HttpStatusCode.NotFound -> RepositoryError.NotFound()
    HttpStatusCode.BadRequest -> RepositoryError.Validation(errors = emptyList())
    HttpStatusCode.Conflict -> RepositoryError.Conflict()
    else -> RepositoryError.Internal(message = "HTTP $value", code = ErrorCode.http(value))
}
