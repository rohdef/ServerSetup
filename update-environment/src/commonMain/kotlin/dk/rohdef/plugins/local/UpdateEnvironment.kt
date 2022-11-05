package dk.rohdef.plugins.local

import arrow.core.Either
import arrow.core.flatMap
import configuration.Parameters
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction
import engine.EngineError
import engine.EnvironmentUpdates

class UpdateEnvironment : StepAction {
    override val actionId = ActionId("updateEnvironment@v1")

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<UpdateEnvironmentError, EnvironmentUpdates> {
        return parameters.value.asSequence()
            .fold(Either.Right(emptyMap<String, String>()) as Either<UpdateEnvironmentError, EnvironmentUpdates>) { acc, entry ->
                acc.flatMap {
                    val parameter = entry.value
                    when (parameter) {
                        is Parameters.Integer -> Either.Right(it + (entry.key to parameter.value.toString()))
                        is Parameters.String -> Either.Right(it + (entry.key to parameter.value))
                        is Parameters.List -> Either.Left(UpdateEnvironmentError.ListNotAllowed(entry.key))
                        is Parameters.Map -> Either.Left(UpdateEnvironmentError.MapNotAllowed(entry.key))
                    }
                }
            }
    }
}

sealed interface UpdateEnvironmentError : EngineError {
    data class MapNotAllowed(val value: String) : UpdateEnvironmentError

    data class ListNotAllowed(val value: String) : UpdateEnvironmentError
}