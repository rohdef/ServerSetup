package mocks.engine

import arrow.core.Either
import configuration.installation.Step
import engine.EngineError
import engine.Environment
import engine.EnvironmentUpdates
import engine.StepRunner
import mocks.NextResult

class TestStepRunner(
    var nextResult: NextResult = NextResult.Success(emptyMap<String, String>())
) : StepRunner {
    override suspend fun run(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates> {
        val currentNextResult = nextResult
        return when(currentNextResult) {
            is NextResult.Failure -> Either.Left(currentNextResult.error)
            is NextResult.Success<*> -> Either.Right(currentNextResult.result as EnvironmentUpdates)
        }
    }
}