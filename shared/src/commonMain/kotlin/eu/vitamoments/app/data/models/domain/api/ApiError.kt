package eu.vitamoments.app.data.models.domain.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: ErrorCode,
    val message: String = "No error message provided",
    val fieldErrors: List<ApiFieldError> = emptyList(),
    val traceId: String? = null
)
