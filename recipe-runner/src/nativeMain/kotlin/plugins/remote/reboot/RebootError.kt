package plugins.remote.reboot

import configuration.ParameterError
import engine.EngineError
import utilities.SystemUtilityError

sealed interface RebootError : EngineError {
    data class CannotExecuteCommand<T : SystemUtilityError>(
        val error: T
    ) : RebootError

    data class CouldNotParseRebootConfiguration<T : ParameterError>(
        val error: T
    ) : RebootError
}