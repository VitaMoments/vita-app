package eu.vitamoments.app.data.mapper.extension_functions

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import eu.vitamoments.app.data.repository.RepositoryResponse

suspend inline fun <Domain, reified Dto : Any> ApplicationCall.respondRepositoryResponse(
    result: RepositoryResponse<Domain>,
    successStatus: HttpStatusCode = HttpStatusCode.OK,
    crossinline mapBody: (Domain) -> Dto
) {
    when (result) {
        is RepositoryResponse.Success -> {
            val dto = mapBody(result.body)
            respond(status = successStatus, dto)
        }

        is RepositoryResponse.Error.Conflict -> {
            respond(HttpStatusCode.Conflict, mapOf("message" to result.message, "key" to result.key))
        }

        is RepositoryResponse.Error.InvalidData -> {
            respond(HttpStatusCode.BadRequest, mapOf("message" to result.message, "key" to result.key))
        }

        is RepositoryResponse.Error.Validation -> {
            respond(HttpStatusCode.BadRequest, mapOf("message" to result.message))
        }

        is RepositoryResponse.Error.Unauthorized -> {
            respond(
                HttpStatusCode.Unauthorized,
                mapOf("message" to result.message)
            )
        }

        is RepositoryResponse.Error.NotFound -> {
            respond(
                HttpStatusCode.NotFound,
                mapOf("message" to result.message)
            )
        }

        is RepositoryResponse.Error.Internal -> {
            respond(
                HttpStatusCode.InternalServerError,
                mapOf("message" to (result.message))
            )
        }
    }
}

