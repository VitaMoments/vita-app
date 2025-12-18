package eu.vitamoments.app.modules

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import eu.vitamoments.app.routes.api.authRoutes
import eu.vitamoments.app.routes.api.timelineRoutes
import eu.vitamoments.app.routes.api.userRoutes
import java.io.File

fun Application.configureRouting() {
    routing {
        staticFiles(
            remotePath = "/assets/avatars",
            dir = File("uploads/avatars")
        )

        get("/") {
            application.log.info("route hit")
            call.respondText("Ktor: Hello from Shared DTO")
        }

        apiRoutes()
    }
}

private fun Routing.apiRoutes() {
    route("/api") {
        authRoutes()

        authenticate("cookie-jwt-authentication") {
            userRoutes()
            timelineRoutes()
        }
    }
}