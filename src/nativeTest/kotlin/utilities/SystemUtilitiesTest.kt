package utilities

import utilities.SystemUtilities
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SystemUtilitiesTest {
    private val utilities = SystemUtilities()

    @Test
    fun `Generate simple command`() {
        val command = "ls"

        val generatedCommand = utilities.generateCommand(command)
        val expectedCommand = "ls"

        generatedCommand.shouldBe(expectedCommand)
    }

    @Test
    fun `Command with parameters`() {
        val command = "kubectl"
        val parameters = listOf(
            "get",
            "all",
            "--all-namespaces",
        )

        val generatedCommand = utilities.generateCommand(command, *parameters.toTypedArray())
        val expectedCommand = """kubectl "get" "all" "--all-namespaces""""

        generatedCommand.shouldBe(expectedCommand)
    }

    @Test
    fun `Escapes spaces in command path`() {
        val command = "/usr/bin/some silly path to/executable"
        val parameters = listOf<String>()

        val generatedCommand = utilities.generateCommand(command, *parameters.toTypedArray())
        val expectedCommand = """/usr/bin/some\ silly\ path\ to/executable"""

        generatedCommand.shouldBe(expectedCommand)
    }

    @Test
    fun `Escapes quotes in parameters`() {
        val command = "echo"
        val parameters = listOf(
            """ { "json": "should", "be": "escaped" } """
        )

        val generatedCommand = utilities.generateCommand(command, *parameters.toTypedArray())
        val expectedCommand = """echo " { \"json\": \"should\", \"be\": \"escaped\" } """"

        generatedCommand.shouldBe(expectedCommand)
    }
}