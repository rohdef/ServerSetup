package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.right
import com.soywiz.korio.util.UUID
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.PathUtility
import dk.rohdef.rfpath.utility.PathUtilityError
import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.posix.readlink

class OkioPathUtility private constructor(
    private val fileSystem: FileSystem,
    private val applicationDirectory: okio.Path,
    private val workDirectory: okio.Path,
    private val temporaryDirectory: okio.Path,
) : PathUtility {
    override suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory> =
        OkioDirectory.directory(fileSystem, applicationDirectory)

    override suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory> =
        OkioDirectory.directory(fileSystem, workDirectory)

    override suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        return either {
            val directory = OkioDirectory.directory(fileSystem, temporaryDirectory)
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

    companion object {
        fun createPathUtility(
            fileSystem: FileSystem = FileSystem.SYSTEM,
            applicationDirectory: okio.Path = applicationDirecrtory(),
            workDirectory: okio.Path = ".".toPath(),
            temporaryDirectory: okio.Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY,
        ): Either<Unit, PathUtility> {
            return OkioPathUtility(
                fileSystem,
                applicationDirectory,
                workDirectory,
                temporaryDirectory
            ).right()
        }

        fun createPathUtilityUnsafe(
            fileSystem: FileSystem = FileSystem.SYSTEM,
            applicationDirectory: okio.Path = applicationDirecrtory(),
            workDirectory: okio.Path = ".".toPath(),
            temporaryDirectory: okio.Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY,
        ): PathUtility {
            val utility = createPathUtility(
                fileSystem,
                applicationDirectory,
                workDirectory,
                temporaryDirectory,
            )

            when (utility) {
                is Either.Right -> return utility.value
                is Either.Left -> throw IllegalArgumentException("Could not create path utility: ${utility}")
            }
        }

        private fun applicationDirecrtory(): okio.Path {
            return executableFile()
                .toPath()
                .parent!!
        }

        private fun executableFile(): String {
            return posixReadlink("/proc/self/exe")
                ?: posixReadlink("/proc/curproc/file")
                ?: posixReadlink("/proc/self/path/a.out")
                ?: "./a.out"
        }

        private fun posixReadlink(path: String): String? = memScoped {
            val addr = allocArray<ByteVar>(PATH_MAX)
            val finalSize = readlink(path, addr, PATH_MAX.convert()).toInt()
            if (finalSize < 0) {
                null
            } else {
                addr.toKString()
            }
        }
    }
}