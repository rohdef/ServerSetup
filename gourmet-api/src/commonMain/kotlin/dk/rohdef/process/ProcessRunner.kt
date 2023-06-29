package dk.rohdef.process

import arrow.core.Either

interface ProcessRunner {
    fun executeCommand(
        command: String,
    ): Either<SystemUtilityError, String>
}