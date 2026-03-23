package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.readAllBytesAndDispose
import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.models.domain.media.MediaAssetResponse
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.repository.RepositoryError
import eu.vitamoments.app.data.repository.RepositoryResult
import eu.vitamoments.app.data.media.MediaService
import eu.vitamoments.app.data.repository.UserRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid
import kotlinx.io.readByteArray
import org.koin.ktor.ext.inject

fun Route.mediaRoutes() {
    val mediaService: MediaService by inject()

    post("/media") {
        val currentUserId = call.requireUserId()

        var referenceId: Uuid? = null
        var referenceType: MediaReferenceType? = null
        var purpose: MediaPurposeType? = null
        var privacy: PrivacyStatus? = null

        var originalFileName: String? = null
        var contentType: String? = null
        var fileBytes: ByteArray? = null

        val multipart = call.receiveMultipart(
            formFieldLimit = 1024L * 1024L * 100L
        )
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "referenceId" -> {
                            referenceId = runCatching { Uuid.parse(part.value) }.getOrNull()
                        }
                        "referenceType" -> {
                            referenceType = runCatching { MediaReferenceType.valueOf(part.value) }.getOrNull()
                        }
                        "purpose" -> {
                            purpose = runCatching { MediaPurposeType.valueOf(part.value) }.getOrNull()
                        }
                        "privacy" -> {
                            privacy = runCatching { PrivacyStatus.valueOf(part.value) }.getOrNull()
                        }
                    }
                    part.dispose()
                }

                is PartData.FileItem -> {
                    originalFileName = part.originalFileName
                    contentType = part.contentType?.toString()
                    fileBytes = part.readAllBytesAndDispose()
                }

                else -> part.dispose()
            }
        }

        if (referenceId == null || referenceType == null || purpose == null || privacy == null || fileBytes == null) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                RepositoryError.Validation(
                    errors = listOf(
                        RepositoryError.FieldError(
                            field = "request",
                            message = "Missing or invalid upload fields"
                        )
                    )
                ).toApiError()
            )
        }

        when (
            val result = mediaService.uploadMedia(
                referenceId = referenceId!!,
                referenceType = referenceType!!,
                purpose = purpose!!,
                privacy = privacy!!,
                originalFileName = originalFileName,
                contentType = contentType,
                bytes = fileBytes!!,
                createdBy = currentUserId
            )
        ) {
            is RepositoryResult.Error -> {
                call.respond(
                    result.error.statusCode(),
                    result.error.toApiError()
                )
            }

            is RepositoryResult.Success -> {
                val media = result.body

                call.respond(
                    HttpStatusCode.Created,
                    MediaAssetResponse(
                        uuid = media.uuid,
                        url = "/api/media/${media.uuid}",
                        contentType = media.contentType,
                        sizeBytes = media.sizeBytes,
                        purpose = media.purpose,
                        privacy = media.privacy,
                        referenceType = media.referenceType,
                        referenceId = media.referenceId
                    )
                )
            }


        }
    }

    delete("/api/media/{id}") {
        val currentUserId = call.requireUserId()

        val idParam = call.parameters["id"]
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                RepositoryError.BadRequest(
                    errors = listOf(
                        RepositoryError.FieldError("id", "Missing media id")
                    )
                ).toApiError()
            )

        val mediaId = runCatching { Uuid.parse(idParam) }.getOrNull()
            ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                RepositoryError.BadRequest(
                    errors = listOf(
                        RepositoryError.FieldError("id", "Invalid media id")
                    )
                ).toApiError()
            )

        when (val result = mediaService.deleteMedia(mediaId, currentUserId)) {
            is RepositoryResult.Error -> {
                call.respond(
                    result.error.statusCode(),
                    result.error.toApiError()
                )
            }

            is RepositoryResult.Success -> {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    get("/api/media/{id}") {
        val idParam = call.parameters["id"]
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                RepositoryError.BadRequest(
                    errors = listOf(
                        RepositoryError.FieldError("id", "Missing media id")
                    )
                ).toApiError()
            )

        val mediaId = runCatching { Uuid.parse(idParam) }.getOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                RepositoryError.BadRequest(
                    errors = listOf(
                        RepositoryError.FieldError("id", "Invalid media id")
                    )
                ).toApiError()
            )

        val requesterUserId = call.requireUserId()

        when (val result = mediaService.readMedia(mediaId, requesterUserId)) {
            is RepositoryResult.Error -> {
                call.respond(
                    result.error.statusCode(),
                    result.error.toApiError()
                )
            }

            is RepositoryResult.Success -> {
                val (_, storedMedia) = result.body

                val bytes = storedMedia.source.use { source ->
                    source.readByteArray()
                }

//                storedMedia.contentLength?.let {
//                    call.response.header(HttpHeaders.ContentLength, it.toString())
//                }

                call.respondBytes(
                    bytes = bytes,
                    contentType = ContentType.parse(storedMedia.contentType)
                )
            }
        }
    }
}