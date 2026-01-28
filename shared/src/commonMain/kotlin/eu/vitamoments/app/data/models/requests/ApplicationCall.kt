package eu.vitamoments.app.data.models.requests

import eu.vitamoments.app.data.models.domain.api.ApiError
import eu.vitamoments.app.data.models.domain.api.ApiFieldError
import eu.vitamoments.app.data.repository.RepositoryResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.util.reflect.typeInfo

suspend inline fun <reified T> ApplicationCall.respondRepository(rr: RepositoryResponse<T>, succesStatusCode: HttpStatusCode = HttpStatusCode.OK) {
    when (rr) {
        is RepositoryResponse.Success -> {
                respond(
                    status = succesStatusCode,
                    message = rr.body,
                    messageType = typeInfo<T>()
                )
        }

        is RepositoryResponse.Error.RequestLimitReached ->
            respond(HttpStatusCode.TooManyRequests, rr.toApiError())

        is RepositoryResponse.Error.Conflict ->
            respond(HttpStatusCode.Conflict, rr.toApiError())

        is RepositoryResponse.Error.Validation ->
            respond(HttpStatusCode.BadRequest, rr.toApiError())

        is RepositoryResponse.Error.Unauthorized ->
            respond(HttpStatusCode.Unauthorized, rr.toApiError())

        is RepositoryResponse.Error.NotFound ->
            respond(HttpStatusCode.NotFound, rr.toApiError())

        is RepositoryResponse.Error.Internal ->
            respond(HttpStatusCode.InternalServerError, rr.toApiError())
    }
}

fun RepositoryResponse.Error.toApiError(): ApiError {
    val fieldErrors = when (this) {
        is RepositoryResponse.Error.Validation -> this.errors
        is RepositoryResponse.Error.Conflict -> this.errors
        else -> emptyList()
    }.map { ApiFieldError(field = it.field, message = it.message) }

    return ApiError(
        code = this.code,
        message = this.message,
        fieldErrors = fieldErrors,
        traceId = this.traceId
    )
}
