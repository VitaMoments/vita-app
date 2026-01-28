package eu.vitamoments.app.data.models.requests.auth_requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)