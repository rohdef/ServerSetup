package dk.rohdef.plugins.remote

import arrow.core.Either
import arrow.core.continuations.either
import configuration.ParameterError
import configuration.Parameters

data class Host(
    val hostname: String,
    val port: Int,
    val username: String,
    val password: String,
) {
    companion object {
        suspend fun create(parameters: Parameters.Map): Either<ParameterError, Host> {
            return either {
                val hostname = parameters.stringValue(
                    "hostname",
                )
                val port = parameters.integerValue(
                    "port",
                    22,
                )
                val username = parameters.stringValue(
                    "username",
                )
                val password = parameters.stringValue(
                    "password",
                )

                Host(
                    hostname.bind(),
                    port.bind(),
                    username.bind(),
                    password.bind(),
                )
            }
        }
    }
}