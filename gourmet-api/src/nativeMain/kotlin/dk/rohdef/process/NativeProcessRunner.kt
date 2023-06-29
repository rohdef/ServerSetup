package dk.rohdef.process

import arrow.core.Either
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import mu.KotlinLogging
import platform.posix.FILE
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

class NativeProcessRunner : ProcessRunner {
    private val logger = KotlinLogging.logger("NativeProcessRunner")

    override fun executeCommand(
        command: String,
    ): Either<SystemUtilityError, String> {
        val commandToExecute = "${command} 2>&1"
        val filePointer = popen(commandToExecute, "r")
        if (filePointer == null) {
            logger.error { "Here's what I could not find: $command" }
            return Either.Left(SystemUtilityError.CouldNotRunCommand(command))
        }

        val stdout = readProcessOutput(filePointer)

        val status = pclose(filePointer)
        return if (status == 0) {
            Either.Right(stdout)
        } else {
            logger.debug { "Error running command [$status]: $stdout" }
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