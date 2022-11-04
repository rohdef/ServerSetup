package plugins.remote

import arrow.core.Either
import plugins.remote.install.Path
import utilities.SystemUtilities
import utilities.SystemUtilityError

fun SystemUtilities.executeSshCommand(
    host: Host,
    command: String,
): Either<SystemUtilityError, String> {
    val sshPrefixed = listOf(
        "-p", host.password,
        "ssh",
        "-o", "ConnectTimeout=10",
        "${host.username}@${host.hostname}",
        "-p", "${host.port}",
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
        "-p", host.password,
        "ssh",
        "-o", "ConnectTimeout=10",
        "${host.username}@${host.hostname}",
        "-p", "${host.port}",
        generateCommand(
            executable, *parameters
        )
    )

    return this.executeCommand("sshpass", *sshPrefixed.toTypedArray())
}

fun SystemUtilities.scpToRemote(
    host: Host,
    sources: List<Path>,
    destination: String,
): Either<SystemUtilityError, String> {
    val sourceArguments = sources.map { it.absolutePath }
        .map { generateCommand(it) }

    val sshPrefixed =
        listOf(
            "-p",
            host.password,
            "scp",
            "-o", "ConnectTimeout=10",
            "-r",
            "-P", "${host.port}"
        ) +
                sourceArguments +
                listOf("${host.username}@${host.hostname}:${destination}")

    return this.executeCommand("sshpass", *sshPrefixed.toTypedArray())
}