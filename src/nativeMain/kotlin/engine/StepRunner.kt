package engine

import arrow.core.Either
import configuration.installation.Step

interface StepRunner {
    suspend fun runStep(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates>
}