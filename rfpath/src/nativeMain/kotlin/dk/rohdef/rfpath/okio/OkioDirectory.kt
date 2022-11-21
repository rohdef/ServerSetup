package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.getUnixPermission
import com.soywiz.korio.file.setUnixPermission
import com.soywiz.korio.file.std.LocalVfsNative
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.NewFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.korio.toPermissions
import dk.rohdef.rfpath.korio.toVfsPermissions
import dk.rohdef.rfpath.permissions.Permissions
import okio.FileSystem

class OkioDirectory private constructor(
    private val fileSystem: FileSystem,
    private val path: okio.Path
) : Path.Directory {
    override val absolutePath: String = path.toString()

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermission(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }

    private val vfs: VfsFile = VfsFile(LocalVfsNative(async = true), path.toString())

    override suspend fun list(): List<Path<*>> {
        TODO("not implemented")
    }

    override suspend fun newFile(fileName: String): Either<NewFileError, Path.File> {
        return either {
            val file = OkioFile.createFile(fileSystem, path.resolve(fileName))
                .bind()

            file
        }
    }

    companion object {
        fun directory(fileSystem: FileSystem, path: okio.Path): Either<DirectoryInstance, Path.Directory> {
            val metadata = fileSystem.metadataOrNull(path)

            return if (metadata == null) {
                DirectoryInstance.EntityIsNonExisting(path.toString()).left()
            } else if (metadata.isDirectory) {
                OkioDirectory(fileSystem, path).right()
            } else {
                DirectoryInstance.EntityIsAFile(path.toString()).left()
            }
        }
    }
}