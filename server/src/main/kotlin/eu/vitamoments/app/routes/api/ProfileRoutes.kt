@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.mapper.extension_functions.respondRepositoryResponse
import eu.vitamoments.app.data.mapper.toDto
import eu.vitamoments.app.data.repository.RepositoryResponse
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.UserRepository
import eu.vitamoments.app.services.CropRect
import eu.vitamoments.app.services.ProfileImageService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.http.content.staticFiles
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import kotlin.getValue
import kotlin.uuid.ExperimentalUuidApi

private class HttpStatusException(
    val status: HttpStatusCode,
    override val message: String
) : RuntimeException(message)

fun Route.profileRoutes() {
    val config = JWTConfigLoader.loadOrThrow()
    val authRepo: ServerAuthRepository by inject()
    val userRepo: UserRepository by inject()

    route("/profile") {
        post("/image") {
            val userId = call.requireUserId()

            val maxUploadBytes = 50L * 1024L * 1024L

            var cropX: Int? = null
            var cropY: Int? = null
            var cropW: Int? = null
            var cropH: Int? = null
            var avatarSize: Int = 512

            var tmpFile: File? = null
            var ct: ContentType? = null

            val multipart = call.receiveMultipart()

            try {
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "cropX" -> cropX = part.value.toIntOrNull()
                                "cropY" -> cropY = part.value.toIntOrNull()
                                "cropW" -> cropW = part.value.toIntOrNull()
                                "cropH" -> cropH = part.value.toIntOrNull()
                                "avatarSize" -> avatarSize =
                                    (part.value.toIntOrNull() ?: 512).coerceIn(64, 2048)
                            }
                        }

                        is PartData.FileItem -> {
                            if (part.name != "file") {
                                part.dispose()
                                return@forEachPart
                            }

                            ct = part.contentType?.withoutParameters()
                            val allowed = setOf(ContentType.Image.JPEG, ContentType.Image.PNG)
                            if (ct !in allowed) {
                                part.dispose()
                                throw HttpStatusException(
                                    HttpStatusCode.UnsupportedMediaType,
                                    "Only JPG/JPEG and PNG allowed"
                                )
                            }


                            val tmpPath = Files.createTempFile("profile-upload-", ".img")
                            val f = tmpPath.toFile()

                            part.streamProvider().use { input ->
                                f.outputStream().use { output ->
                                    copyWithLimit(input, output, maxUploadBytes)
                                }
                            }

                            tmpFile = f
                        }

                        else -> Unit
                    }
                    part.dispose()
                }
                val file = tmpFile ?: return@post call.respond(HttpStatusCode.BadRequest, "No file")

                val crop = if (cropX != null && cropY != null && cropW != null && cropH != null) {
                    CropRect(cropX!!, cropY!!, cropW!!, cropH!!)
                } else null

                val service = ProfileImageService()
                val url = service.saveProfilePhoto(
                    userId = userId.toString(),
                    tempUploadFile = file,
                    crop = crop,
                    avatarSize = avatarSize
                )
                file.delete()

                val result = userRepo.updateImageUrl(userId, url)

                call.respondRepositoryResponse(result = result) { user -> user.toDto() }
            } catch (e: HttpStatusException) {
                tmpFile?.delete()
                call.respond(e.status, e.message)
            } catch (e: IllegalArgumentException) {
                tmpFile?.delete()
                call.respondRepositoryResponse(RepositoryResponse.Error.InvalidData("payload", "File too large")){}
            } catch (e: Exception) {
                tmpFile?.delete()
                call.respond(HttpStatusCode.InternalServerError, "Upload failed")
            }
        }
    }
}


private fun copyWithLimit(input: InputStream, output: OutputStream, limit: Long) {
    val buf = ByteArray(8192)
    var total = 0L
    while (true) {
        val r = input.read(buf)
        if (r <= 0) break
        total += r
        if (total > limit) throw IllegalArgumentException("File too large (>${limit / (1024 * 1024)}MB)")
        output.write(buf, 0, r)
    }
}