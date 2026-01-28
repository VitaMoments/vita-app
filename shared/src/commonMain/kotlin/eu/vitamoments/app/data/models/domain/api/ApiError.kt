package eu.vitamoments.app.data.models.domain.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String? = null,
    val fieldErrors: List<ApiFieldError> = emptyList(),
    val traceId: String? = null
)
