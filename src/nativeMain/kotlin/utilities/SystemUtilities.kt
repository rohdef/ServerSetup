package utilities

import arrow.core.Either

interface SystemUtilities {
    fun generateCommand(
        executable: String,
        vararg parameters: String,
    ): String

    fun executeCommand(
        executable: String,
        vararg parameters: String,
    ): Either<SystemUtilityError, String>

    fun executeCommand(
        command: String
    ): Either<SystemUtilityError, String>
}