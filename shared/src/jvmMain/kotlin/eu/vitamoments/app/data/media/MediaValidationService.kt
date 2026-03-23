package eu.vitamoments.app.data.media

class MediaValidationService(
    private val maxBytes: Int = 100 * 1024 * 1024
) {
    private val allowedContentTypes = setOf(
        "image/jpeg",
        "image/png",
        "image/webp"
    )

    fun validateImage(
        contentType: String?,
        bytes: ByteArray
    ): Result<Unit> {
        if (contentType.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Missing content type"))
        }
        if (contentType !in allowedContentTypes) {
            return Result.failure(IllegalArgumentException("Unsupported content type: $contentType"))
        }
        if (bytes.isEmpty()) {
            return Result.failure(IllegalArgumentException("Uploaded file is empty"))
        }
        if (bytes.size > maxBytes) {
            return Result.failure(IllegalArgumentException("File too large. Max is $maxBytes bytes"))
        }

        return Result.success(Unit)
    }

    fun extensionFor(contentType: String): String {
        return when (contentType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> error("Unsupported content type: $contentType")
        }
    }
}