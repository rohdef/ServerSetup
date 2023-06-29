package dk.rohdef.gourmet.mocks.plugins

import arrow.core.Either
import com.soywiz.korio.util.UUID
import configuration.Parameters
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction
import engine.EngineError
import engine.EnvironmentUpdates
import dk.rohdef.gourmet.mocks.NextResult

class TestAction(
    var nextResult: NextResult = NextResult.Success(emptyMap<String, String>()),
    override val actionId: ActionId = ActionId(UUID.randomUUID().toString()),
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