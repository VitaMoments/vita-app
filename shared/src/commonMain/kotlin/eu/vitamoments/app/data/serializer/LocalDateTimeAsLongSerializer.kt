package eu.vitamoments.app.data.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

object LocalDateTimeAsLongSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTimeAsLong", PrimitiveKind.LONG)

    override fun serialize(
        encoder: Encoder,
        value: LocalDateTime
    ) {
        val millis = value
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds()

        encoder.encodeLong(millis)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val millis = decoder.decodeLong()
        return Instant
            .fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC)
    }
}