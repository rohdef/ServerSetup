package engine

import configuration.installation.Step
import it.czerwinski.kotlin.util.Either

interface StepRunner {
    fun run(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates>
}