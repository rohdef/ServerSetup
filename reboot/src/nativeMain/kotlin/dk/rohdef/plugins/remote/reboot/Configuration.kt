package dk.rohdef.plugins.remote.reboot

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.flatMap
import configuration.ParameterError
import configuration.Parameters
import dk.rohdef.plugins.remote.Host

data class Configuration(
    val host: Host,
    val waitForReboot: Reboot.WaitForReboot,
    val scriptPath: String,
) {
    companion object {
        suspend fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
            return either {
                val host = parameters.mapValue("host")
                    .flatMap { Host.create(it) }

                val wait = parameters.enumValue("waitForReboot", Reboot.WaitForReboot.DO_NOT_WAIT)
                val scriptPath = parameters.stringValue("scriptPath", "do-configure")

                Configuration(
                    host.bind(),
                    wait.bind(),
                    scriptPath.bind(),
                )
            }
        }
    }
}