package configuration.installation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@kotlinx.serialization.Serializable(with = JobIdSerializer::class)
data class JobId(val id: String)

object JobIdSerializer : KSerializer<JobId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JobId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JobId) = encoder.encodeString(value.id)

    override fun deserialize(decoder: Decoder): JobId = JobId(decoder.decodeString())
}