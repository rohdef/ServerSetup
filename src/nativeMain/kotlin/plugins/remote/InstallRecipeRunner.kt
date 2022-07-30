package plugins.remote

import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction
import it.czerwinski.kotlin.util.Either

object InstallRecipeRunner : StepAction {
    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        TODO("not implemented")
    }
}