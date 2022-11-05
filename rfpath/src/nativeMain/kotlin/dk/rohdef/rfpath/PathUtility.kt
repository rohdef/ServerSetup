package dk.rohdef.rfpath

import arrow.core.Either

interface PathUtility {
    suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File>

    suspend fun applicationDirectory(): Path.Directory
    suspend fun workDirectory(): Path.Directory

    companion object {
        fun defaultUtilities(): PathUtility {
            return KorioPathUtility()
        }
    }
}

sealed interface PathUtilityError {
    sealed interface CreateTemporaryFileError : PathUtilityError {
        object CannotCreateFile : CreateTemporaryFileError
        object CannotGetTemporaryDirectory : CreateTemporaryFileError
    }
}