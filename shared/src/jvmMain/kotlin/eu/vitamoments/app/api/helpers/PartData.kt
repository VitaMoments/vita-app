package eu.vitamoments.app.api.helpers

import io.ktor.http.content.PartData
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

suspend fun PartData.FileItem.readChannel(): ByteReadChannel {
    return provider()
}

suspend fun PartData.FileItem.readAllBytesAndDispose(): ByteArray {
    return try {
        val channel = provider()
        channel.readRemaining().readByteArray()
    } finally {
        dispose()
    }
}