package plugins.local

import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Right
import mu.KotlinLogging

object Debug : StepAction {
    private val logger = KotlinLogging.logger {}

    override fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        logger.info { "Debug action is run with:" }

        logger.info { formatParameters(parameters) }

        return Right(emptyMap())
    }

    private fun formatParameters(parameters: Parameters, indentation: Int = 0): String {
        val indent = "  ".repeat(indentation)

        return when (parameters) {
            is Parameters.Boolean -> parameters.value.toString()
            is Parameters.Integer -> parameters.value.toString()
            is Parameters.List -> {
                val formatting = parameters.value
                    .map { formatParameters(it, indentation + 1) }
                    .map { "${indent}${it}" }
                    .joinToString(",\n")

                return formatting
            }

            is Parameters.Map -> {
                val formatting = parameters.value
                    .mapValues { formatParameters(it.value, indentation + 1) }
                    .map { "${indent}${it.key}: ${it.value}" }
                    .joinToString(",\n")

                return "\n$formatting"
            }

            is Parameters.String -> parameters.value
        }
    }
}