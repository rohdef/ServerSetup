package plugins.remote

import arrow.core.Either
import utilities.SystemUtilities
import utilities.SystemUtilityError

fun SystemUtilities.executeSshCommand(
    host: Host,
    command: String,
): Either<SystemUtilityError, String> {
    val sshPrefixed = listOf(
        "-p",
        host.password,
        "ssh",
        "${host.username}@${host.hostname}",
        "-p",
        "${host.port}",
        command,
    )

    return this.executeCommand("sshpass", *sshPrefixed.toTypedArray())
}

fun SystemUtilities.executeSshCommand(
    host: Host,
    executable: String,
    vararg parameters: String,
): Either<SystemUtilityError, String> {
    val sshPrefixed = listOf(
        "-p",
        host.password,
        "ssh",
        "${host.username}@${host.hostname}",
        "-p",
        "${host.port}",
        generateCommand(
            executable, *parameters
        )
    )

    return this.executeCommand("sshpass", *sshPrefixed.toTypedArray())
}