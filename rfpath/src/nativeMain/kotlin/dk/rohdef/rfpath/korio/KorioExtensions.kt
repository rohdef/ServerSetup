package dk.rohdef.rfpath.korio

import com.soywiz.korio.file.Vfs
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.permissions.Permission

fun Vfs.UnixPermissions.toPermissions(): Permissions {
    return Permissions(
        this.owner.toPermission(),
        this.group.toPermission(),
        this.other.toPermission(),
    )
}

fun Vfs.UnixPermission.toPermission(): Set<Permission> {
    val permissions = mutableSetOf<Permission>()

    if (this.readable) permissions.add(Permission.READ)
    if (this.writable) permissions.add(Permission.WRITE)
    if (this.executable) permissions.add(Permission.EXECUTE)

    return permissions.toSet()
}

fun Permissions.toVfsPermissions(): Vfs.UnixPermissions {
    return Vfs.UnixPermissions(
        this.owner.toVfsPermission(),
        this.group.toVfsPermission(),
        this.other.toVfsPermission(),
    )
}

fun Set<Permission>.toVfsPermission(): Vfs.UnixPermission {
    val read = this.contains(Permission.READ)
    val write = this.contains(Permission.WRITE)
    val execute = this.contains(Permission.EXECUTE)

    return Vfs.UnixPermission(read, write, execute)
}