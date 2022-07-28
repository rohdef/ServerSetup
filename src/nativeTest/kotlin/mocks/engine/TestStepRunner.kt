package mocks.engine

import configuration.engine.EngineError
import configuration.engine.Environment
import configuration.engine.EnvironmentUpdates
import configuration.engine.StepRunner
import configuration.installation.Step
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import mocks.NextResult

class TestStepRunner(
    var nextResult: NextResult = NextResult.Success(emptyMap<String, String>())
) : StepRunner {
    override fun run(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates> {
        val currentNextResult = nextResult
        return when(currentNextResult) {
            is NextResult.Failure -> Left(currentNextResult.error)
            is NextResult.Success<*> -> Right(currentNextResult.result as EnvironmentUpdates)
        }
    }
}