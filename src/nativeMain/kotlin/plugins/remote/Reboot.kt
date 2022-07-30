package plugins.remote

import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction
import it.czerwinski.kotlin.util.Either

object Reboot : StepAction {
    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {


        val rebootCommand = "export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"
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
}

