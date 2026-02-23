package eu.vitamoments.app.routes.api

import eu.vitamoments.app.data.repository.BlogRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.blogRoutes() {
    val repo: BlogRepository by inject()

    route("/timeline") {

    }
}