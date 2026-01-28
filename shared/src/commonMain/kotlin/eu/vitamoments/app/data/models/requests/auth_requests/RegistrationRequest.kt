package eu.vitamoments.app.data.models.requests.auth_requests

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)
