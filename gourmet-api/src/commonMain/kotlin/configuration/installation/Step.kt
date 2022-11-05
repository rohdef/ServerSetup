package configuration.installation

import configuration.Parameters
import plugins.ActionId

@kotlinx.serialization.Serializable
data class Step(
    val name: String,
    val uses: ActionId,
    val parameters: Parameters.Map = Parameters.Map(emptyMap()),
)
