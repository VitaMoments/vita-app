package eu.vitamoments.app.data.media

import kotlinx.io.Source

data class StoredMedia(
    val source: Source,
    val contentType: String,
    val contentLength: Long?
)