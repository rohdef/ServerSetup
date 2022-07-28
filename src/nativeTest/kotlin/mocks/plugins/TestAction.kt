package mocks.plugins

import configuration.Parameters
import configuration.engine.EngineError
import configuration.engine.EnvironmentUpdates
import configuration.plugins.StepAction
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import mocks.NextResult

class TestAction(
    var nextResult: NextResult = NextResult.Success(emptyMap<String, String>())
) : StepAction {
    private val _executions = mutableListOf<Execution>()

    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        val currentNextResult = nextResult
        val result = when(currentNextResult) {
            is NextResult.Failure -> Left(currentNextResult.error)
            is NextResult.Success<*> -> Right(currentNextResult.result as EnvironmentUpdates)
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