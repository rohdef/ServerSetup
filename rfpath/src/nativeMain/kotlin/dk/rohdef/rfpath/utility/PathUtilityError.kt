package dk.rohdef.rfpath.utility

sealed interface PathUtilityError {
    sealed interface CreateTemporaryFileError : PathUtilityError {
        object CannotCreateFile : CreateTemporaryFileError
        object CannotGetTemporaryDirectory : CreateTemporaryFileError
    }
}