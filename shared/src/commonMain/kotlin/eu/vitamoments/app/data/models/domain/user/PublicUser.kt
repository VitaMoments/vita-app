package eu.vitamoments.app.data.models.domain.user

import kotlin.uuid.Uuid

/**
 * PublicUser is the user provided when searching users etc.
 */
data class PublicUser(
    override val uuid: Uuid,
    override val alias: String,
    override val bio: String? = null,
    override val imageUrl: String? = null
) : User
