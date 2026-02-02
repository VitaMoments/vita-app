package eu.vitamoments.app.data.models.requests

import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.RepositoryResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

suspend fun ApplicationCall.respondError(err: RepositoryError) {
    respond(err.statusCode(), err.toApiError())
}

suspend inline fun <reified T> ApplicationCall.respondRepository(
    rr: RepositoryResult<T>,
    successStatusCode: HttpStatusCode = HttpStatusCode.OK,
    messageType: TypeInfo = typeInfo<T>()
) {
    when (rr) {
        is RepositoryResult.Success -> {
            respond(
                status = successStatusCode,
                message = rr.body,
                messageType = messageType
            )
        }
        is RepositoryResult.Error -> respondError(rr.error)
    }
}

suspend inline fun <reified T> ApplicationCall.handleResult(
    result: RepositoryResult<T>,
    messageType: TypeInfo = typeInfo<T>(),
    successStatusCode: HttpStatusCode = HttpStatusCode.OK,
    noinline onSuccess: (suspend ApplicationCall.(T) -> Unit)? = null,
    noinline onError: (suspend ApplicationCall.(RepositoryError) -> Unit)? = null
) {
    when (result) {
        is RepositoryResult.Success -> {
            if (onSuccess != null) onSuccess(result.body)
            else respondRepository(result, successStatusCode, messageType = messageType)
        }

        is RepositoryResult.Error -> {
            if (onError != null) onError(result.error)
            else respondError(result.error)
        }
    }
}
