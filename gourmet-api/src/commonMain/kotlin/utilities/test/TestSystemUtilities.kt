package utilities.test

import arrow.core.Either
import utilities.SystemUtilities
import utilities.SystemUtilityError

class TestSystemUtilities(
    var nextResult: Either<SystemUtilityError, String> = Either.Right("Yay")
) : SystemUtilities {
    private val _executions = mutableListOf<Execution>()
    val executions: List<Execution>
        get() {
            return _executions.toList()
        }

    override fun generateCommand(executable: String, vararg parameters: String): String {
        val escapedExecutable = executable.replace(" ", "\\ ")
        val escapedParameters = listOf(*parameters)
            .map { it.replace("\"", "\\\"") }
            .map { "\"$it\"" }

        val commandParts = listOf(escapedExecutable) + escapedParameters

        return commandParts.joinToString(" ")
    }

    override fun executeCommand(executable: String, vararg parameters: String): Either<SystemUtilityError, String> {
        val command = generateCommand(executable, *parameters)
        return executeCommand(command)
    }

    override fun executeCommand(command: String): Either<SystemUtilityError, String> {
        _executions.add(Execution(command))

        return nextResult
    }

    data class Execution(
        val command: String,
    )
}