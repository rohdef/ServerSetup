package mocks.plugins

import arrow.core.Either
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import mocks.NextResult
import plugins.StepAction

class TestAction(
    var nextResult: NextResult = NextResult.Success(emptyMap<String, String>())
) : StepAction {
    private val _executions = mutableListOf<Execution>()

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        val currentNextResult = nextResult
        val result = when(currentNextResult) {
            is NextResult.Failure -> Either.Left(currentNextResult.error)
            is NextResult.Success<*> -> Either.Right(currentNextResult.result as EnvironmentUpdates)
        }

        _executions.add(
            Execution(
                parameters,
                result,
            )
        )

        return result
    }

    val executions: List<Execution>
        get() = _executions.toList()

    data class Execution(
        val parameters: Parameters.Map,
        val result: Either<EngineError, EnvironmentUpdates>,
    )
}