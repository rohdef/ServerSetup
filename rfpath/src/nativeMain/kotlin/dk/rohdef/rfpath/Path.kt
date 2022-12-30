package dk.rohdef.rfpath

import arrow.core.Either
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.permissions.UserGroup

sealed interface Path<T : Path<T>> {
    val absolutePath: String

    suspend fun addPermission(userGroup: UserGroup, permission: Permission): Either<DirectoryError, T> {
        val updatedPermissions = currentPermission()
            .addPermission(userGroup, permission)

        return setPermissions(updatedPermissions)
    }

    suspend fun removePermission(userGroup: UserGroup, permission: Permission): Either<DirectoryError, T> {
        val updatedPermissions = currentPermission()
            .removePermission(userGroup, permission)
        return setPermissions(updatedPermissions)
    }

    suspend fun setPermissions(userGroup: UserGroup, permissions: Set<Permission>): Either<DirectoryError, T> {
        val updatedPermissions = currentPermission()
            .changePermissions(userGroup, permissions)
        return setPermissions(updatedPermissions)
    }

    suspend fun addPermissions(permissions: Permissions): Either<DirectoryError, T> {
        TODO()
    }

    suspend fun removePermissions(permissions: Permissions): Either<DirectoryError, T> {
        TODO()
    }

    suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, T>

    suspend fun currentPermission(): Permissions

    interface Directory : Path<Directory> {
        suspend fun list(): Either<PathError, List<Path<*>>>

        suspend fun newFile(fileName: String): Either<NewFileError, File>
    }

    interface File : Path<File> {
        suspend fun write(text: String): Either<FileError, File>
    }
}