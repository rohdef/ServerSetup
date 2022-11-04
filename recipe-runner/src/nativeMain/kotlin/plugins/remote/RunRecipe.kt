package plugins.remote

import arrow.core.Either
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.ActionId
import plugins.StepAction

class RunRecipe : StepAction {
    override val actionId = ActionId("runRecipe@v1")

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        TODO("not implemented")
    }
}