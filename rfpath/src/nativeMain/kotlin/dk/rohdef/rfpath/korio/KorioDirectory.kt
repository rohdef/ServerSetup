package dk.rohdef.rfpath.korio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.right
import arrow.core.traverse
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.getUnixPermission
import com.soywiz.korio.file.setUnixPermission
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions

class KorioDirectory private constructor(
    private val vfs: VfsFile,
) : Path.Directory {
    override val absolutePath = vfs.absolutePath

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermission(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }

    override suspend fun list(): Either<PathError, List<Path<*>>> {
        return vfs.listSimple()
            // TODO: 29/10/2022 rohdef - is there a better way to deal with type safety?
            .traverse {
                when {
                    it.isDirectory() -> directory(it)
                    it.isFile() -> KorioFile.file(it)
                    else -> throw IllegalArgumentException("Only files and directories should be possible`")
                }
            }
    }

    override suspend fun newFile(fileName: String): Either<NewFileError, Path.File> {
        return either {
            val filePath = vfs.get(fileName)
            if (filePath.exists()) {
                Either.Left(NewFileError.FileExists(fileName))
                    .bind<Path.File>()
            }

            // TODO: 30/10/2022 rohdef - ensure file and not directory - how?
            // is that even poossible???
            KorioFile.file(filePath)
                .mapLeft { TODO("Type issues") }
                .bind()
        }
    }

    companion object {
        suspend fun directory(vfsFile: VfsFile): Either<DirectoryInstance, Path.Directory> {
            return if (!vfsFile.isDirectory()) {
                // TODO: 12/11/2022 rohdef - better error + constructor... (maybe?)
                Either.Left(DirectoryInstance.EntityIsAFile(vfsFile.absolutePath))
            } else {
                Either.Right(KorioDirectory(vfsFile))
            }
        }

        suspend fun directoryUnsafe(vfsFile: VfsFile): Path.Directory {
            val directory = directory(vfsFile)
            return when (directory) {
                is Either.Right -> directory.value
                is Either.Left -> throw IllegalArgumentException("Argh")
            }
        }
    }
}