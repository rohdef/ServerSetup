package dk.rohdef.rfpath.permissions

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly

class PermissionsTest : FunSpec({
    val emptyPermissions = Permissions(
        emptySet(),
        emptySet(),
        emptySet(),
    )
    val defaultLinux = Permissions(
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ),
    )
    val allPermissions = Permissions(
        Permission.values().toSet(),
        Permission.values().toSet(),
        Permission.values().toSet(),
    )

    val defaultPermissions = setOf(
        emptyPermissions,
        allPermissions,
        defaultLinux
    )

    fun testPermissions(
        permissions: Permissions,
        owner: Set<Permission>,
        group: Set<Permission>,
        other: Set<Permission>,
    ) {
        permissions.owner
            .shouldContainExactly(owner)
        permissions.group
            .shouldContainExactly(group)
        permissions.other
            .shouldContainExactly(other)
    }

    context("Change permissions") {
        val permissionCombinations = sequenceOf(
            setOf(Permission.READ),
            setOf(Permission.WRITE),
            setOf(Permission.EXECUTE),
            setOf(Permission.READ, Permission.WRITE),
            setOf(Permission.READ, Permission.EXECUTE),
            setOf(Permission.WRITE, Permission.EXECUTE),
            setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        )

        val testData = defaultPermissions.flatMap { perms ->
            permissionCombinations.toSet().map { perms to it }
        }.asSequence()

        context("for owner") {
            withData(testData) {
                val testPermissions = it.first
                val ownerPermissions = it.second
                val permissions = testPermissions.changePermissions(UserGroup.OWNER, ownerPermissions)

                testPermissions(
                    permissions,
                    ownerPermissions,
                    testPermissions.group,
                    testPermissions.other,
                )
            }
        }

        context("for group") {
            withData(testData) {
                val testPermissions = it.first
                val groupPermissions = it.second
                val permissions = testPermissions.changePermissions(UserGroup.GROUP, groupPermissions)

                testPermissions(
                    permissions,
                    testPermissions.owner,
                    groupPermissions,
                    testPermissions.other,
                )
            }
        }

        context("for others") {
            withData(testData) {
                val testPermissions = it.first
                val otherPermissions = it.second
                val permissions = testPermissions.changePermissions(UserGroup.OTHER, otherPermissions)

                testPermissions(
                    permissions,
                    testPermissions.owner,
                    testPermissions.group,
                    otherPermissions,
                )
            }
        }
    }

    context("Add permissions") {
        data class TestCase(
            val permissions: Permissions,
            val permissionToAdd: Permission,
            val expected: Set<Permission>
        )

        context("for owner") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf(Permission.READ)
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf(Permission.WRITE)
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf(Permission.EXECUTE)
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf(Permission.READ, Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ, Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.addPermission(UserGroup.OWNER, it.permissionToAdd)

                testPermissions(
                    permissions,
                    it.expected,
                    it.permissions.group,
                    it.permissions.other,
                )
            }
        }

        context("for group") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf(Permission.READ)
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf(Permission.WRITE)
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf(Permission.EXECUTE)
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf(Permission.READ, Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ, Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.addPermission(UserGroup.GROUP, it.permissionToAdd)

                testPermissions(
                    permissions,
                    it.permissions.owner,
                    it.expected,
                    it.permissions.other,
                )
            }
        }

        context("for other") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf(Permission.READ)
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf(Permission.WRITE)
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf(Permission.EXECUTE)
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf(Permission.READ)
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ, Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ, Permission.EXECUTE)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.addPermission(UserGroup.OTHER, it.permissionToAdd)

                testPermissions(
                    permissions,
                    it.permissions.owner,
                    it.permissions.group,
                    it.expected,
                )
            }
        }
    }

    context("Remove permissions") {
        data class TestCase(
            val permissions: Permissions,
            val permissionToRemove: Permission,
            val expected: Set<Permission>
        )

        context("for owner") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf()
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf(Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.removePermission(UserGroup.OWNER, it.permissionToRemove)

                testPermissions(
                    permissions,
                    it.expected,
                    it.permissions.group,
                    it.permissions.other,
                )
            }
        }

        context("for group") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf()
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf(Permission.WRITE)
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.removePermission(UserGroup.GROUP, it.permissionToRemove)

                testPermissions(
                    permissions,
                    it.permissions.owner,
                    it.expected,
                    it.permissions.other,
                )
            }
        }

        context("for other") {
            val testData = sequenceOf(
                TestCase(
                    emptyPermissions, Permission.READ, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.WRITE, setOf()
                ),
                TestCase(
                    emptyPermissions, Permission.EXECUTE, setOf()
                ),

                TestCase(
                    defaultLinux, Permission.READ, setOf()
                ),
                TestCase(
                    defaultLinux, Permission.WRITE, setOf(Permission.READ)
                ),
                TestCase(
                    defaultLinux, Permission.EXECUTE, setOf(Permission.READ)
                ),

                TestCase(
                    allPermissions, Permission.READ, setOf(Permission.WRITE, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.WRITE, setOf(Permission.READ, Permission.EXECUTE)
                ),
                TestCase(
                    allPermissions, Permission.EXECUTE, setOf(Permission.READ, Permission.WRITE)
                ),
            )

            withData(testData) {
                val permissions = it.permissions.removePermission(UserGroup.OTHER, it.permissionToRemove)

                testPermissions(
                    permissions,
                    it.permissions.owner,
                    it.permissions.group,
                    it.expected,
                )
            }
        }
    }
})