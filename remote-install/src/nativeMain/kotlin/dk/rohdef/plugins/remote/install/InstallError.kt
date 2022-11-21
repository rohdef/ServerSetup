package dk.rohdef.plugins.remote.install

import configuration.ParameterError
import dk.rohdef.rfpath.DirectoryInstance
import engine.EngineError
import utilities.SystemUtilityError

sealed interface InstallError : EngineError {
    object CannotWriteAskpassFile : InstallError

    data class CannotExecuteCommand<T : SystemUtilityError>(
        val error: T
    ) : InstallError

    data class CouldNotParseConfiguration<T : ParameterError>(
        val error: T
    ) : InstallError
}

sealed interface ApplicationDirectoryUnavailable : InstallError {
    companion object {
        fun fromDirectoryInstance(directoryInstance: DirectoryInstance): ApplicationDirectoryUnavailable {
            when (directoryInstance) {
                is DirectoryInstance.EntityIsAFile -> TODO()
                is DirectoryInstance.EntityIsNonExisting -> TODO()
            }

            TODO()
        }
    }
}