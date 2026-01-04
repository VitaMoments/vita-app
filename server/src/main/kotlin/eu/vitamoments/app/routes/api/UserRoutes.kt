@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.routes.api

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
import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.mapper.extension_functions.respondRepositoryResponse
import eu.vitamoments.app.data.mapper.toDto
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.repository.RepositoryResponse
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.UserRepository
import io.ktor.server.http.content.staticFiles
import jdk.internal.vm.ScopedValueContainer.call
import org.koin.ktor.ext.inject
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.getValue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun Route.userRoutes() {
    staticFiles("/uploads", File("uploads"))
    val config = JWTConfigLoader.loadOrThrow()
    val authRepo: ServerAuthRepository by inject()
    val userRepo: UserRepository by inject()

    route("/users") {


    }
}