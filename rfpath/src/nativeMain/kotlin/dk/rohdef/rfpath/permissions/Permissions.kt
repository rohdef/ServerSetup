package dk.rohdef.rfpath.permissions

data class Permissions(
    val owner: Set<Permission>,
    val group: Set<Permission>,
    val other: Set<Permission>,
) {
    fun changePermissions(userGroup: UserGroup, permissions: Set<Permission>): Permissions {
        return when (userGroup) {
            UserGroup.OWNER -> this.copy(owner = permissions)
            UserGroup.GROUP -> this.copy(group = permissions)
            UserGroup.OTHER -> this.copy(other = permissions)
        }
    }

    fun addPermission(userGroup: UserGroup, permission: Permission): Permissions {
        val updatedPermissions = when (userGroup) {
            UserGroup.OWNER -> owner + permission
            UserGroup.GROUP -> group + permission
            UserGroup.OTHER -> other + permission
        }

        return changePermissions(userGroup, updatedPermissions)
    }

    fun removePermission(userGroup: UserGroup, permission: Permission): Permissions {
        val updatedPermissions = when (userGroup) {
            UserGroup.OWNER -> owner - permission
            UserGroup.GROUP -> group - permission
            UserGroup.OTHER -> other - permission
        }

        return changePermissions(userGroup, updatedPermissions)
    }
}