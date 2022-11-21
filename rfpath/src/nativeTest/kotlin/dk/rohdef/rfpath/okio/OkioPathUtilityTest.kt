package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.PathUtility
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
class OkioPathUtilityTest {
    private val testHelpers = OkioTestHelpers()
    private val fileSystem = testHelpers.fileSystem()
    private val pathUtility: PathUtility = OkioPathUtility.createPathUtilityUnsafe(
        fileSystem,
        testHelpers.applicationDirectoryPath,
        testHelpers.workingDirectoryPath,
        testHelpers.temporaryDirectoryPath,
    )

    @Test
    fun `application and working directory`() = runTest {
        val applicationDirectory = pathUtility.applicationDirectory()
            .shouldBeRight()
        val workDirectory = pathUtility.workDirectory()
            .shouldBeRight()

        applicationDirectory.absolutePath
            .shouldBe(testHelpers.applicationDirectory)
        applicationDirectory
            .shouldBeInstanceOf<Path.Directory>()

        workDirectory.absolutePath
            .shouldBe(testHelpers.workingDirectory)
        workDirectory
            .shouldBeInstanceOf<Path.Directory>()
    }

    @Test
    fun `create temporary file`() = runTest {
        val temporaryFile = pathUtility.createTemporaryFile()

        val file = temporaryFile.shouldBeRight()
        file
            .shouldBeInstanceOf<Path.File>()
        file.absolutePath
            .shouldStartWith(testHelpers.temporaryDirectory)
    }
}