package dk.rohdef.rfpath

import arrow.core.Either
import arrow.core.computations.either
import com.soywiz.korio.file.std.applicationVfs
import com.soywiz.korio.file.std.cwdVfs
import com.soywiz.korio.file.std.tempVfs
import com.soywiz.korio.util.UUID

class KorioPathUtility : PathUtility {
    override suspend fun applicationDirectory(): Path.Directory = KorioDirectoryWrapper.directoryUnsafe(applicationVfs)

    override suspend fun workDirectory(): Path.Directory = KorioDirectoryWrapper.directoryUnsafe(cwdVfs)

    override suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        return either {
            val directory = KorioDirectoryWrapper
                .directory(tempVfs)
                // TODO: 05/11/2022 rohdef - better error handling
                .mapLeft { PathUtilityError.CreateTemporaryFileError.CannotGetTemporaryDirectory }
                .bind()
            val uuid = UUID.randomUUID().toString()
            // TODO: 29/10/2022 rohdef - get actual app name
            val applicationName = "gourmet"

            directory
                .newFile("${applicationName}-${uuid}")
                // TODO: 05/11/2022 rohdef - better error handling
                .mapLeft { PathUtilityError.CreateTemporaryFileError.CannotCreateFile }
                .bind()
        }
    }
}