package plugins.remote.install

import configuration.ParameterError
import engine.EngineError
import utilities.SystemUtilityError

sealed interface InstallError : EngineError {
    data class CannotExecuteCommand<T : SystemUtilityError>(
        val error: T
    ) : InstallError

    data class CouldNotParseConfiguration<T : ParameterError>(
        val error: T
    ) : InstallError
}