package utilities

import arrow.core.Either
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.FILE
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

class LinuxSystemUtilities : SystemUtilities {
    override fun generateCommand(
        executable: String,
        vararg parameters: String,
    ): String {
        val escapedExecutable = executable.replace(" ", "\\ ")
        val escapedParameters = listOf(*parameters)
            .map { it.replace("\"", "\\\"") }
            .map { "\"$it\"" }

        val commandParts = listOf(escapedExecutable) + escapedParameters

        return commandParts.joinToString(" ")
    }

    override fun executeCommand(
        executable: String,
        vararg parameters: String,
    ): Either<SystemUtilityError, String> {
        val command = generateCommand(executable, *parameters)
        return executeCommand(command)
    }

    override fun executeCommand(
        command: String
    ): Either<SystemUtilityError, String> {
        val commandToExecute = "${command} 2>&1"
        val filePointer = popen(commandToExecute, "r")
        if (filePointer == null) {
            return Either.Left(SystemUtilityError.CouldNotRunCommand(command))
        }

        val stdout = readProcessOutput(filePointer)

        val status = pclose(filePointer)
        return if (status == 0) {
            Either.Right(stdout)
        } else {
            Either.Left(SystemUtilityError.ErrorRunningCommand(command, status, stdout))
        }
    }

    private fun readProcessOutput(filePointer: CPointer<FILE>): String {
        return buildString {
            val buffer = ByteArray(4096)
            while (true) {
                val input = fgets(buffer.refTo(0), buffer.size, filePointer) ?: break
                append(input.toKString())
            }
        }.trim()
    }
}