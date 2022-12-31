package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import arrow.core.traverse
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.getUnixPermission
import com.soywiz.korio.file.setUnixPermission
import com.soywiz.korio.file.std.LocalVfsNative
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions
import okio.FileSystem

class OkioDirectory private constructor(
    private val fileSystem: FileSystem,
    private val path: okio.Path
) : Path.Directory {
    override val absolutePath: String = fileSystem.canonicalize(path).toString()

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermission(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }

    private val vfs: VfsFile = VfsFile(LocalVfsNative(async = true), path.toString())

    override suspend fun list(): Either<PathError, List<Path<*>>> {
        return fileSystem.list(path)
            .traverse {
                val metadata = fileSystem.metadataOrNull(it)

                if (metadata == null) {
                    DirectoryInstance.EntityIsNonExisting(it.toString()).left()
                } else if (metadata.isDirectory) {
                    directory(fileSystem, it)
                } else {
                    OkioFile.file(fileSystem, it)
                }
            }
    }

    override suspend fun newFile(fileName: String): Either<NewFileError, Path.File> {
        return either {
            val file = OkioFile.createFile(fileSystem, path.resolve(fileName))
                .bind()

            file
        }
    }

    override suspend fun resolve(subpath: String): Either<PathError, Path<*>> {
        val resolvedPath = path.resolve(subpath)

        val metadata = fileSystem.metadataOrNull(resolvedPath)

        return if (metadata == null) {
            DirectoryInstance.EntityIsNonExisting(resolvedPath.toString()).left()
        } else if (metadata.isDirectory) {
            directory(fileSystem, resolvedPath)
        } else {
            OkioFile.file(fileSystem, resolvedPath)
        }
    }

    override fun toString(): String {
        return "OkioDirectory(path=$path)"
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