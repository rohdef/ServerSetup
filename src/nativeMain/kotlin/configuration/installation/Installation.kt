package configuration.installation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@kotlinx.serialization.Serializable(with = InstallationSerializer::class)
data class Installation(
    // TODO change to ordered map, current implementation is dangerous
    val jobs: Map<JobId, Job>,
)

object InstallationSerializer : KSerializer<Installation> {
    override val descriptor: SerialDescriptor = InstallationWrapperSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Installation) {
        val surrogate = InstallationWrapperSurrogate(InstallationSurrogate(value.jobs))
        encoder.encodeSerializableValue(InstallationWrapperSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Installation {
        val surrogate = decoder.decodeSerializableValue(InstallationWrapperSurrogate.serializer())
        return Installation(surrogate.installation.jobs)
    }
}

@kotlinx.serialization.Serializable
data class InstallationWrapperSurrogate(
    val installation: InstallationSurrogate
)

@kotlinx.serialization.Serializable
data class InstallationSurrogate(
    val jobs: Map<JobId, Job>
)