package plugins.remote.reboot

import arrow.core.Either
import arrow.core.computations.either
import configuration.ParameterError
import configuration.Parameters
import plugins.remote.Host

data class Configuration(
    val host: Host,
    val waitForReboot: Reboot.WaitForReboot,
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