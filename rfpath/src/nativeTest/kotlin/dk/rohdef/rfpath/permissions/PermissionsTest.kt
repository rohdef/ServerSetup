package dk.rohdef.rfpath.permissions

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Ignore
import kotlin.test.Test

class PermissionsTest {
    private val emptyPermissions = Permissions(
        emptySet(),
        emptySet(),
        emptySet(),
    )

    @Test
    @Ignore
    fun `change owner permissions`() {
        val read = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val write = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val execute = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val readWrite = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val readExecute = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val writeExecute = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
        val readWriteExecute = emptyPermissions.changePermissions(UserGroup.OWNER, setOf())
    }

    @Test
    @Ignore
    fun `add read permission`() {
        val owner = emptyPermissions.addPermission(UserGroup.OWNER, Permission.READ)
        val group = emptyPermissions.addPermission(UserGroup.GROUP, Permission.READ)
        val other = emptyPermissions.addPermission(UserGroup.OTHER, Permission.READ)

        owner.owner.shouldContainExactly(setOf(Permission.READ))
        owner.group.shouldBeEmpty()
        owner.other.shouldBeEmpty()

        group.owner.shouldBeEmpty()
        group.group.shouldContainExactly(setOf(Permission.READ))
        group.other.shouldBeEmpty()

        other.owner.shouldBeEmpty()
        other.group.shouldBeEmpty()
        other.other.shouldContainExactly(setOf(Permission.READ))
    }

    @Test
    @Ignore
    fun `add write permission`() {
        val owner = emptyPermissions.addPermission(UserGroup.OWNER, Permission.WRITE)
        val group = emptyPermissions.addPermission(UserGroup.GROUP, Permission.WRITE)
        val other = emptyPermissions.addPermission(UserGroup.OTHER, Permission.WRITE)

        owner.owner.shouldContainExactly(setOf(Permission.WRITE))
        owner.group.shouldBeEmpty()
        owner.other.shouldBeEmpty()

        group.owner.shouldBeEmpty()
        group.group.shouldContainExactly(setOf(Permission.WRITE))
        group.other.shouldBeEmpty()

        other.owner.shouldBeEmpty()
        other.group.shouldBeEmpty()
        other.other.shouldContainExactly(setOf(Permission.WRITE))
    }

    @Test
    @Ignore
    fun `remove simplistic`() {
    }
}