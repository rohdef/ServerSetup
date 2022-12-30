package dk.rohdef.rfpath.utility

import dk.rohdef.rfpath.PathError

sealed interface PathUtilityError {
    sealed interface CreateTemporaryFileError : PathUtilityError {
        object CannotCreateFile : CreateTemporaryFileError
        object CannotGetTemporaryDirectory : CreateTemporaryFileError
    }

    sealed interface ResolveDirectoryError : PathUtilityError
    sealed interface ResolveFileError : PathUtilityError
}