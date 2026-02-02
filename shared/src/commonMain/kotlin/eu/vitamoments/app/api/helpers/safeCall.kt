package eu.vitamoments.app.api.helpers

import eu.vitamoments.app.data.mapper.toRepositoryResult
import eu.vitamoments.app.data.models.domain.api.ErrorCode
import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.RepositoryResult
import io.ktor.client.plugins.*
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> safeCall(
    crossinline request: suspend () -> HttpResponse
): RepositoryResult<T> {
    return try {
        val response = request()
        response.toRepositoryResult<T>()
    } catch (e: CancellationException) {
        // nooit "opslokken"; coroutines moeten kunnen cancelen
        throw e
    } catch (e: ResponseException) {
        // gebeurt bij expectSuccess=true en status is non-2xx
        e.response.toRepositoryResult<T>()
    } catch (e: SerializationException) {
        RepositoryResult.Error(
            RepositoryError.Internal(
                message = "Serialization error: ${e.message}",
                code = ErrorCode.SERIALIZATION
            )
        )
    } catch (e: IOException) {
        RepositoryResult.Error(
            RepositoryError.Internal(
                message = "Network error: ${e.message ?: "offline"}",
                code = ErrorCode.NETWORK
            )
        )
    } catch (t: Throwable) {
        RepositoryResult.Error(
            RepositoryError.Internal(
                message = t.message ?: "Unknown error",
                code = ErrorCode.CLIENT
            )
        )
    }
}
