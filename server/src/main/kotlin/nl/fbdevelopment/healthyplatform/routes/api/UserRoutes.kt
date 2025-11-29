@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.routes.api

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import nl.fbdevelopment.healthyplatform.api.helpers.requireUserId
import nl.fbdevelopment.healthyplatform.config.JWTConfigLoader
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.respondRepositoryResponse
import nl.fbdevelopment.healthyplatform.data.mapper.toDto
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.data.repository.RepositoryResponse
import nl.fbdevelopment.healthyplatform.data.repository.ServerAuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.UserRepository
import org.koin.ktor.ext.inject
import java.io.File
import kotlin.getValue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun Route.userRoutes() {
    val config = JWTConfigLoader.loadOrThrow()
    val authRepo: ServerAuthRepository by inject()
    val userRepo: UserRepository by inject()

    route("/users") {

        get {
            val userId : Uuid = call.requireUserId()
            call.respondRepositoryResponse(userRepo.getUserById(userId)) { it.toDto() }
        }

        post("/avatar") {
            val userId : Uuid = call.requireUserId()
            val userResponse = userRepo.getUserById(userId)

            when(userResponse) {
                is RepositoryResponse.Success<User> -> {
                    val user: User = userResponse.body

                    val uploadDir = File("uploads/avatars")
                    if (!uploadDir.exists()) uploadDir.mkdirs()

                    val multipart = call.receiveMultipart()
                    var newFileName: String? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                if (part.name == "avatar") {
                                    val contentType = part.contentType
                                    if (contentType == null || !contentType.toString().startsWith("image/")) {
                                        call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Only images allowed"))
                                        part.dispose()
                                        return@forEachPart
                                    }

                                    val ext = when (contentType.contentSubtype.lowercase()) {
                                        "jpeg", "jpg" -> "jpg"
                                        "png" -> "png"
                                        "webp" -> "webp"
                                        else -> "bin"
                                    }

                                    val fileName = "${Uuid.random()}.$ext"
                                    val file = File(uploadDir, fileName)

                                    val channel = part.provider()
                                    val packet = channel.readRemaining()
                                    val bytes = packet.readByteArray()
                                    file.writeBytes(bytes)

                                    newFileName = fileName
                                }
                                part.dispose()
                            }
                            else -> part.dispose()
                        }
                    }

                    if (newFileName == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("message" to "No avatar file found"))
                        return@post
                    }

                    user.imageUrl?.let { oldUrl ->
                        if (oldUrl.isNotBlank()) {
                            val oldFile = File(uploadDir, oldUrl)
                            if (oldFile.exists()) {
                                oldFile.delete()
                            }
                        }
                    }

                    user.imageUrl = newFileName

                    userRepo.updateUser(user)


                }
                else -> call.respondRepositoryResponse<_,_>(userResponse) { }
            }
        }
    }
}