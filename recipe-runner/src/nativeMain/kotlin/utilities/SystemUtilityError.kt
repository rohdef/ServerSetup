package utilities

sealed interface SystemUtilityError {
    data class CouldNotRunCommand(val command: String) : SystemUtilityError

    data class ErrorRunningCommand(
        val command: String,
        val status: Int,
        val output: String,
    ) : SystemUtilityError
}