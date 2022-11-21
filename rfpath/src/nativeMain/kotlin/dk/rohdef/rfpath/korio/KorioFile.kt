package dk.rohdef.rfpath.korio

import arrow.core.Either
import arrow.core.right
import com.soywiz.korio.file.*
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

class KorioFile private constructor(
    private val vfs: VfsFile,
) : Path.File {
    override val absolutePath = vfs.absolutePath

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.File> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermission(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }

    override suspend fun write(text: String): Either<FileError, Path.File> {
        vfs.writeString(text)
        return this.right()
    }

    companion object {
        suspend fun file(vfsFile: VfsFile): Either<FileError, Path.File> {
            return if (!vfsFile.isFile()) {
                Either.Left(FileError.NotAFile)
            } else {
                Either.Right(KorioFile(vfsFile))
            }
        }

        suspend fun fileUnsafe(vfsFile: VfsFile): Path.File {
            val file = file(vfsFile)
            return when(file) {
                is Either.Right -> file.value
                is Either.Left -> throw IllegalArgumentException("Argh")
            }
        }
    }
}