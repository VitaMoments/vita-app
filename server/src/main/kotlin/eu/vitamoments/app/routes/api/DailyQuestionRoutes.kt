package eu.vitamoments.app.routes.api

import eu.vitamoments.app.api.helpers.requireUserId
import eu.vitamoments.app.data.models.requests.daily_questions_requests.SubmitDailyQuestionAnswerRequest
import eu.vitamoments.app.data.models.requests.handleResult
import eu.vitamoments.app.data.repository.DailyQuestionRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.dailyQuestionRoutes() {
    val repo: DailyQuestionRepository by inject()

    route("/daily-questions") {
        get("/next") {
            val userId: Uuid = call.requireUserId()
            val result = repo.getOrCreateTodayQuestion(userId)
            call.handleResult(result)
        }

        post("/answer") {
            val userId: Uuid = call.requireUserId()
            val request: SubmitDailyQuestionAnswerRequest = call.receive()
            val result = repo.submitAnswer(userId, request)
            call.handleResult(result, successStatusCode = HttpStatusCode.Created)
        }
    }
}
