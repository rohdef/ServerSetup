package configuration.plugins

import configuration.Parameters
import configuration.engine.EngineError
import configuration.engine.EnvironmentUpdates
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

object UpdateEnvironment : StepAction {
    override fun run(
        parameters: Parameters.Map,
    ): Either<UpdateEnvironmentError, EnvironmentUpdates> {
        return parameters.value.asSequence()
            .fold(Right(emptyMap<String, String>()) as Either<UpdateEnvironmentError, EnvironmentUpdates>) { acc, entry ->
                acc.flatMap {
                    val parameter = entry.value
                    when (parameter) {
                        is Parameters.Boolean -> Right(it + (entry.key to parameter.value.toString()))
                        is Parameters.Integer -> Right(it + (entry.key to parameter.value.toString()))
                        is Parameters.List -> Left(UpdateEnvironmentError.ListNotAllowed(entry.key))
                        is Parameters.Map -> Left(UpdateEnvironmentError.MapNotAllowed(entry.key))
                        is Parameters.String -> Right(it + (entry.key to parameter.value))
                    }
                }
            }
    }
}

sealed interface UpdateEnvironmentError : EngineError {
    data class MapNotAllowed(val value: String) : UpdateEnvironmentError

    data class ListNotAllowed(val value: String) : UpdateEnvironmentError
}
