package plugins.remote

import arrow.core.Either
import arrow.core.computations.either
import configuration.ParameterError
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction
import utilities.SystemUtilities
import utilities.SystemUtilityError

object Reboot : StepAction {
    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        val utils = SystemUtilities()
        Configuration.create(parameters)
            .map { configuration ->
                val rebootCommand =
                    "export SUDO_ASKPASS=/home/${configuration.username}/${configuration.scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"

                val result = utils.executeCommand(rebootCommand)

                when (result) {
                    is Either.Right -> TODO()
                    is Either.Left -> {
                        val error = result.value
                        when (error) {
                            is SystemUtilityError.CouldNotRunCommand -> TODO()
                            is SystemUtilityError.ErrorRunningCommand -> error.status
                        }
                    }
                }


            }
        /*
         *  try:
              self._runSshCommand(
                environment,
                f"export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"
            )
        except:
            pass #expected, ssh will terminate connection
        time.sleep(1)
         */

        TODO("not implemented")
    }

    data class Configuration(
        val hostname: String,
        val username: String,
        val port: Int,
        val waitForSystem: WaitForSystem,
        val scriptPath: String,
    ) {
        companion object {
            suspend fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
                return either {
                    val hostname = parameters.stringValue("hostname")
                    val username = parameters.stringValue("username")
                    val port = parameters.integerValue("port", 22)
                    val wait = parameters.enumValue("wait", WaitForSystem.DO_NOT_WAIT)
                    val scriptPath = parameters.stringValue("scriptPath", "blah")

                    Configuration(
                        hostname.bind(),
                        username.bind(),
                        port.bind(),
                        wait.bind(),
                        scriptPath.bind(),
                    )
                }
            }
        }
    }

    enum class WaitForSystem {
        WAIT,
        DO_NOT_WAIT,
    }
}

