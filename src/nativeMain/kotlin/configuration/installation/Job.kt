package configuration.installation

@kotlinx.serialization.Serializable
data class Job(
    val name: String,
    val steps: List<Step>,
)