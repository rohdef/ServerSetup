package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.FileInstance
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

@ExperimentalCoroutinesApi
class OkioFileTest {
    private val testHelpers = OkioTestHelpers()
    private val fileSystem = testHelpers.fileSystem()

    private val testFile = testHelpers.temporaryDirectoryPath
        .resolve(testHelpers.dummyFilename1)

    @Test
    fun `successfully create file`() {
        val file = testFile()
            .shouldBeRight()
        file.absolutePath
            .shouldBe(testFile.toString())
    }

    @Test
    fun `not accept directory`() {
        val directoryPath = testHelpers.workingDirectoryPath

        val error = OkioFile.file(fileSystem, directoryPath)
            .shouldBeLeft()
        error
            .shouldBe(FileInstance.EntityIsADirectory(directoryPath.toString()))
    }

    @Test
    fun `not accept non-path`() {
        val notAPath = "this is nonsense"
        val directoryPath = testHelpers.workingDirectoryPath
            .resolve(notAPath)

        val error = OkioFile.file(fileSystem, directoryPath)
            .shouldBeLeft()
        error
            .shouldBe(FileInstance.EntityIsNonExisting(directoryPath.toString()))
    }

    @Test
    @Ignore
    fun `Current permissions`() = runTest {
        TODO("Currently untestable due to okio not having a system for permissions")
    }

    @Test
    @Ignore
    fun `Change permissions`() = runTest {
        TODO("Currently untestable due to okio not having a system for permissions")
    }

    private fun testFileUnwrapped() =
        testFile()
            .shouldBeRight()

    private fun testFile() =
        OkioFile.file(fileSystem, testFile)
}