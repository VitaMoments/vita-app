package eu.vitamoments.app.data.models.domain.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiFieldError(
    val field: String,
    val message: String
)