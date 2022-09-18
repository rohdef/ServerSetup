package plugins.remote

data class Host(
    val hostname: String,
    val port: Int,
    val username: String,
    val password: String,
)