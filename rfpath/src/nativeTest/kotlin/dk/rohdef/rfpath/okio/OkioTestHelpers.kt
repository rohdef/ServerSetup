package dk.rohdef.rfpath.okio

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class OkioTestHelpers {
    private val root = "/".toPath()
    val basePath = root.resolve("test")

    val applicationDirectoryPath = basePath
        .resolve("usr")
        .resolve("local")
        .resolve("bin")
    val workingDirectoryPath = basePath
        .resolve("home")
        .resolve("fiktivus")
    val temporaryDirectoryPath = basePath
        .resolve("tmp")

    val applicationDirectory = applicationDirectoryPath.toString()
    val workingDirectory = workingDirectoryPath.toString()
    val temporaryDirectory = temporaryDirectoryPath.toString()

    val dummyFilename1 = "hello.world"
    val dummyFilename2 = "foo"
    val dummyFilename3 = "multi-line"

    fun fileSystem(): FileSystem {
        val fileSystem = FakeFileSystem()

        pathWithDummyFiles(fileSystem, basePath)
        pathWithDummyFiles(fileSystem, applicationDirectoryPath)
        pathWithDummyFiles(fileSystem, workingDirectoryPath)
        pathWithDummyFiles(fileSystem, temporaryDirectoryPath)

        fileSystem.workingDirectory = workingDirectoryPath

        return fileSystem
    }

    private fun pathWithDummyFiles(fileSystem: FileSystem, path: Path) {
        fileSystem.createDirectories(path, true)
        dummyFiles(fileSystem, path)
    }

    private fun dummyFiles(fileSystem: FileSystem, path: Path) {
        fileSystem.write(path.resolve(dummyFilename1), true) {
            writeUtf8("Hello world!")
        }
        fileSystem.write(path.resolve(dummyFilename2), true) {
            writeUtf8("barbaz")
        }
        fileSystem.write(path.resolve(dummyFilename3), true) {
            writeUtf8("""
                Multiple
                Lines
                In
                File
            """.trimIndent())
        }
    }
}