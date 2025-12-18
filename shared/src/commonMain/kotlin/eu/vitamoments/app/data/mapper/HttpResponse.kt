package eu.vitamoments.app.data.mapper

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.repository.RepositoryResponse

import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlin.Exception

suspend inline fun <reified TDto, Domain> HttpResponse.toRepositoryResponse(
    mapBody: (TDto) -> Domain
): RepositoryResponse<Domain> {
    return try {
        when (status) {
            HttpStatusCode.OK,
            HttpStatusCode.Created -> {
                val dto: TDto = body()
                RepositoryResponse.Success(mapBody(dto))
            }

            HttpStatusCode.BadRequest -> {
                RepositoryResponse.Error.InvalidData(
                    key = "general",
                    message = "Invalid data"
                )
            }

            HttpStatusCode.Conflict -> {
                RepositoryResponse.Error.InvalidData(
                    key = "email",
                    message = "Email already exists"
                )
            }

            HttpStatusCode.Unauthorized -> {
                RepositoryResponse.Error.Unauthorized("Unauthorized")
            }

            HttpStatusCode.NotFound -> {
                RepositoryResponse.Error.NotFound("Not found")
            }

            else -> RepositoryResponse.Error.Internal(
                "Unexpected status: ${status.value} ${status.description}"
            )
        }
    } catch (e: SerializationException) {
        RepositoryResponse.Error.Internal("Serialization error: ${e.message}")
    } catch (e: Exception) {
        RepositoryResponse.Error.Internal("Unknown error: ${e.message}")
    }
}

suspend inline fun <reified TDto> HttpResponse.toRepositoryResponse(): RepositoryResponse<TDto> =
    toRepositoryResponse<TDto, TDto> { it }


