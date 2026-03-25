package eu.vitamoments.app.data.models.requests.user_requests

import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMyAccountRequest(
    val alias: String? = null,
    val bio: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val locale: String? = null,
    val timeZone: String? = null,
    val privacyDetails: PrivacyStatus? = null
)

