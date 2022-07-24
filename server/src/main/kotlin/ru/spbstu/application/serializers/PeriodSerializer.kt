package ru.spbstu.application.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Period

object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Period", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Period {
        return Period.ofDays(decoder.decodeInt())
    }

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeInt(value.days)
    }
}
