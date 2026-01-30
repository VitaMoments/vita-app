package eu.vitamoments.app.data.models.domain.api

import eu.vitamoments.app.data.models.domain.api.ErrorCode.Companion.UNKNOWN
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmField
import kotlin.jvm.JvmInline

@Serializable(with = ErrorCode.Serializer::class)
@JvmInline
value class ErrorCode(val value: String) {
    object Serializer : KSerializer<ErrorCode> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ErrorCode", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ErrorCode) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): ErrorCode {
            return ErrorCode(decoder.decodeString())
        }
    }

    companion object Companion {
        val RATE_LIMIT = ErrorCode("RATE_LIMIT")
        val UNAUTHORIZED = ErrorCode("UNAUTHORIZED")
        val NOT_FOUND = ErrorCode("NOT_FOUND")
        val VALIDATION_ERROR = ErrorCode("VALIDATION_ERROR")
        val CONFLICT = ErrorCode("CONFLICT")
        val INTERNAL = ErrorCode("INTERNAL")
        val SERIALIZATION = ErrorCode("SERIALIZATION")
        val NETWORK = ErrorCode("NETWORK")
        val CLIENT = ErrorCode("CLIENT")
        val BAD_REQUEST = ErrorCode("BAD_REQUEST")

        val ACCESS_TOKEN_NOT_FOUND = ErrorCode("ACCESS_TOKEN_NOT_FOUND")
        val ACCESS_TOKEN_NOT_VALID = ErrorCode("ACCESS_TOKEN_NOT_VALID")
        val REFRESH_TOKEN_NOT_FOUND = ErrorCode("REFRESH_TOKEN_NOT_FOUND")
        val REFRESH_TOKEN_NOT_VALID = ErrorCode("REFRESH_TOKEN_NOT_VALID")

        val UNKNOWN = ErrorCode("UNKNOWN")

        // ✅ single source of truth
        private val known: Map<String, ErrorCode> = listOf(
            RATE_LIMIT,
            UNAUTHORIZED,
            NOT_FOUND,
            VALIDATION_ERROR,
            CONFLICT,
            INTERNAL,
            SERIALIZATION,
            NETWORK,
            CLIENT,
            ACCESS_TOKEN_NOT_FOUND,
            ACCESS_TOKEN_NOT_VALID,
            REFRESH_TOKEN_NOT_FOUND,
            REFRESH_TOKEN_NOT_VALID,
            BAD_REQUEST,
            UNKNOWN
        ).associateBy { it.value }

        fun of(raw: String): ErrorCode = known[raw] ?: ErrorCode(raw)

        fun http(status: Int): ErrorCode = ErrorCode("HTTP_$status")
    }
    override fun toString(): String = value
}

fun ErrorCode.isKnown(): Boolean = ErrorCode.of(value) != ErrorCode(value) && value != UNKNOWN.value