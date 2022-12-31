package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.getUnixPermission
import com.soywiz.korio.file.setUnixPermission
import com.soywiz.korio.file.std.LocalVfsNative
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions
import okio.FileSystem

class OkioFile private constructor(
    val fileSystem: FileSystem,
    val path: okio.Path,
) : Path.File {
    override val absolutePath: String = path.toString()

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.File> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermission(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }
    private val vfs: VfsFile = VfsFile(LocalVfsNative(async = true), path.toString())

    override suspend fun write(text: String): Either<FileError, Path.File> {
        fileSystem.write(path) { writeUtf8(text) }

        return this.right()
    }

    companion object {
        fun file(fileSystem: FileSystem, path: okio.Path): Either<FileInstance, Path.File> {
            val metadata = fileSystem.metadataOrNull(path)

            return if (metadata == null) {
                FileInstance.EntityIsNonExisting(path.toString()).left()
            } else if (metadata.isDirectory) {
                FileInstance.EntityIsADirectory(path.toString()).left()
            } else {
                OkioFile(fileSystem, path).right()
            }
        }

        fun createFile(fileSystem: FileSystem, path: okio.Path): Either<NewFileError, Path.File> {
            val metadata = fileSystem.metadataOrNull(path)

            if (metadata != null) {
                return NewFileError.FileExists(path.toString()).left()
            }

            return OkioFile(fileSystem, path).right()
        }
    }
}