package dk.rohdef.rfpath.utility

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.korio.KorioPathUtility
import dk.rohdef.rfpath.Path

interface PathUtility {
    suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File>

    suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory>
    suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory>
}