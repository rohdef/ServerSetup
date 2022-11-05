package dk.rohdef.plugins.remote.install

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import configuration.ParameterError
import configuration.Parameters
import dk.rohdef.plugins.remote.Host

data class Configuration(
    val host: Host,
) {
    companion object {
        suspend fun create(parameters: Parameters.Map): Either<ParameterError, Configuration> {
            return either {
                val host = parameters.mapValue("host")
                    .flatMap { Host.create(it) }

                Configuration(
                    host.bind()
                )
            }
        }
    }
}