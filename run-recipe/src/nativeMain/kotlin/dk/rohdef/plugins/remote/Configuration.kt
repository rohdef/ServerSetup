package dk.rohdef.plugins.remote

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.flatMap
import arrow.core.right
import configuration.ParameterError
import configuration.Parameters

data class Configuration(
    val script: String,
    val jobs: List<String>,
) {
    companion object {
        suspend fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
            return either {
                val script = parameters.stringValue("script")
                    .bind()
                val jobs = parameters.list("jobs")
                    .flatMap { it.stringValues() }
                    .bind()

                Configuration(
                    script,
                    jobs,
                )
            }
        }
    }
}