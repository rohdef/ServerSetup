package plugins.remote

import arrow.core.Either
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction

object InstallRecipeRunner : StepAction {
    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        TODO("not implemented")
    }
}