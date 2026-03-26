package eu.vitamoments.app.data.media

interface MediaStorage {
    suspend fun save(
        objectKey: String,
        bytes: ByteArray,
        contentType: String
    )

    suspend fun read(
        objectKey: String
    ): StoredMedia

    suspend fun delete(
        objectKey: String
    )

    fun buildObjectKey(
        referenceType: String,
        referenceId: String,
        mediaId: String,
        fileExtension: String
    ): String
}