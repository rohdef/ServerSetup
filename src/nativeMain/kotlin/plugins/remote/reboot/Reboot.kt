package plugins.remote.reboot

import arrow.core.Either
import arrow.core.computations.either
import configuration.ParameterError
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import mu.KotlinLogging
import platform.posix.sleep
import plugins.StepAction
import utilities.SystemUtilities
import utilities.SystemUtilityError

class Reboot(
    private val system: SystemUtilities
) : StepAction {
    private val logger = KotlinLogging.logger {}

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<RebootError, EnvironmentUpdates> {
        return either {
            val configuration = Configuration.create(parameters)
                .mapLeft { RebootError.CouldNotParseRebootConfiguration(it) }
                .bind()

            val rebootCommand =
                "export SUDO_ASKPASS=/home/${configuration.host.username}/${configuration.scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"

            val result = system.executeSshCommand(configuration.host, rebootCommand)
            when (result) {
                is Either.Right -> throw Exception("Unexpected successful result - ssh should do a remote terminate on the connection")
                is Either.Left -> handleSshRebootError(result.value).bind()
            }

            logger.info { "Successfully sent reboot command" }
            waitForConnection(configuration).bind()
        }
            .map { emptyMap() }
    }

    private suspend fun waitForConnection(configuration: Configuration): Either<RebootError, Unit> {
        if (configuration.waitForReboot == WaitForReboot.WAIT) {
            sleep(1)
            logger.info { "Waiting for connection" }

            // TODO WAIT
            // TODO look into Dispatcher.IO?
            val selectorManager = SelectorManager()
            val socketResponse = aSocket(selectorManager)
                .tcp()
                .connect(configuration.host.hostname, configuration.host.port)
                .use {
                    it.openReadChannel().readUTF8Line()!!
                }

            logger.info { "Got a socket response: $socketResponse" }
        }

        return Either.Right(Unit)
    }

    private fun handleSshRebootError(error: SystemUtilityError): Either<RebootError, Unit> {
        return when (error) {
            is SystemUtilityError.CouldNotRunCommand -> Either.Left(RebootError.CannotExecuteCommand(error))
            is SystemUtilityError.ErrorRunningCommand -> {
                val closedByRemote = error.output.split(" ")
                    .containsAll(listOf("Connection", "closed", "remote"))

                if (closedByRemote) {
                    Either.Right(Unit)
                } else {
                    Either.Left(RebootError.CannotExecuteCommand(error))
                }
            }
        }
    }

    data class Configuration(
        val host: Host,
        val waitForReboot: WaitForReboot,
        val scriptPath: String,
    ) {
        companion object {
            suspend fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
                return either {
                    val host = parameters.mapValue("host")
                        .map {
                            val hostname = it.stringValue("hostname")
                            val port = it.integerValue("port", 22)
                            val username = it.stringValue("username")
                            val password = it.stringValue("password")

                            Host(
                                hostname.bind(),
                                port.bind(),
                                username.bind(),
                                password.bind(),
                            )
                        }

                    val wait = parameters.enumValue("waitForReboot", WaitForReboot.DO_NOT_WAIT)
                    val scriptPath = parameters.stringValue("scriptPath", "do-configure")

                    Configuration(
                        host.bind(),
                        wait.bind(),
                        scriptPath.bind(),
                    )
                }
            }
        }

        data class Host(
            val hostname: String,
            val port: Int,
            val username: String,
            val password: String,
        )
    }

    enum class WaitForReboot {
        WAIT,
        DO_NOT_WAIT,
    }
}

sealed interface RebootError : EngineError {
    data class CannotExecuteCommand<T : SystemUtilityError>(
        val error: T
    ) : RebootError

    data class CouldNotParseRebootConfiguration<T : ParameterError>(
        val error: T
    ) : RebootError
}

fun SystemUtilities.executeSshCommand(
    host: Reboot.Configuration.Host,
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