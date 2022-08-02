package plugins

import arrow.core.Either
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates

interface StepAction {
    suspend fun run(parameters: Parameters.Map): Either<EngineError, EnvironmentUpdates>
}