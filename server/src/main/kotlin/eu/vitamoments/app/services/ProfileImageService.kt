package eu.vitamoments.app.services

import java.io.File

class ProfileImageService(
    uploadsDir: String = (System.getenv("UPLOADS_DIR") ?: "uploads"),
    private val processor: ImageProcessor = ImageProcessor()
) {
    private val uploadsRoot = File(uploadsDir)
    private val profileDir = File(uploadsRoot, "profile").apply { mkdirs() }

    init {
        File(uploadsRoot, "profile").mkdirs()
    }

    /**
     * Verwerkt de upload en schrijft altijd naar <userId>.jpg (overschrijven = ok).
     * Return: public URL path (bijv. /uploads/profile/123.jpg)
     */
    fun saveProfilePhoto(
        userId: String,
        tempUploadFile: File,
        crop: CropRect?,
        avatarSize: Int
    ): String {
        val original = processor.read(tempUploadFile)

        val cropped = processor.crop(original, crop)
        val resized = processor.resize(cropped, avatarSize, avatarSize)
        val rgb = processor.toOpaqueRgb(resized)

        val outFile = File(profileDir, "$userId.jpg")
        processor.writeJpeg(rgb, outFile)

        return "/uploads/profile/${outFile.name}"
    }
}
