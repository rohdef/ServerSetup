package dk.rohdef.rfpath.korio

import arrow.core.Either
import arrow.core.continuations.either
import com.soywiz.korio.file.std.applicationVfs
import com.soywiz.korio.file.std.cwdVfs
import com.soywiz.korio.file.std.tempVfs
import com.soywiz.korio.util.UUID
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.PathUtility
import dk.rohdef.rfpath.utility.PathUtilityError

class KorioPathUtility : PathUtility {
    override suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory> = KorioDirectory.directory(applicationVfs)

    override suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory> = KorioDirectory.directory(cwdVfs)

    override suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        return either {
            val directory = KorioDirectory.directory(tempVfs)
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