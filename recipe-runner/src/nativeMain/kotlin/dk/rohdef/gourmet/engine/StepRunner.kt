package dk.rohdef.gourmet.engine

import arrow.core.Either
import configuration.installation.Step
import engine.EngineError
import engine.EnvironmentUpdates

interface StepRunner {
    suspend fun runStep(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates>
}