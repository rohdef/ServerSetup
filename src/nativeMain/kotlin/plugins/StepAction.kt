package plugins

import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import it.czerwinski.kotlin.util.Either

interface StepAction {
    fun run(parameters: Parameters.Map): Either<EngineError, EnvironmentUpdates>
}