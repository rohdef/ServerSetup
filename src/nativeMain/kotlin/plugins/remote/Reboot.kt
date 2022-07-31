package plugins.remote

import configuration.ParameterError
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap
import plugins.StepAction

object Reboot : StepAction {
    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {


        val rebootCommand =
            "export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"
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
    ) {
        companion object {
            fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
                return parameters.stringValue("hostname")
                    .flatMap { hostname ->
                        return parameters.stringValue("username")
                            .flatMap { username ->
                                return parameters.integerValue("port", 22)
                                    .map { port -> Configuration(hostname, username, port) }
                            }
                    }
            }
        }
    }
}

