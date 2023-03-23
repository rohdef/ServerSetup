package dk.rohdef.plugins.remote

import arrow.core.Either
import configuration.Parameters
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction
import engine.EngineError
import engine.EnvironmentUpdates

class RunRecipe : StepAction {
    override val actionId = ActionId("runRecipe@v1")

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        val cmd = "export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass ~/{self._scriptPath}/declarative.py -c ~/{script}.yaml {jobCommand}"

        TODO("not implemented")
    }
}