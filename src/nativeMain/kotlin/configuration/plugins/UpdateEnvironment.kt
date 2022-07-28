package configuration.plugins

import configuration.Parameters
import configuration.engine.EngineError
import configuration.engine.EnvironmentUpdates
import it.czerwinski.kotlin.util.Either

object UpdateEnvironment : StepAction {
    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        TODO("not implemented")
    }
}