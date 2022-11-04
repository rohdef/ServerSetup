package plugins.remote.reboot

import arrow.core.Either
import arrow.core.computations.either
import configuration.Parameters
import engine.EnvironmentUpdates
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import mu.KotlinLogging
import platform.posix.sleep
import plugins.ActionId
import plugins.StepAction
import plugins.remote.executeSshCommand
import utilities.SystemUtilities
import utilities.SystemUtilityError

class Reboot(
    private val system: SystemUtilities,
    private val socketFactory: SocketFactory
) : StepAction {
    private val logger = KotlinLogging.logger {}

    override val actionId = ActionId("reboot@v1")

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
            val socketResponse = socketFactory
                .tcpConnect(configuration.host.hostname, configuration.host.port)
                .use {
                    it.openReadChannel()
                        .readUTF8Line()!!
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



    enum class WaitForReboot {
        WAIT,
        DO_NOT_WAIT,
    }
}