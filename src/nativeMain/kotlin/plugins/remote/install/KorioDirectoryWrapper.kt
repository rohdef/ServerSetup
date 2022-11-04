package plugins.remote.install

import arrow.core.Either
import arrow.core.computations.either
import com.soywiz.korio.file.VfsFile

class KorioDirectoryWrapper private constructor(
    private val vfs: VfsFile,
) : Path.Directory {
    override val absolutePath = vfs.absolutePath

    override suspend fun list(): List<Path> {
        return vfs.listSimple()
            // TODO: 29/10/2022 rohdef - is there a better way to deal with type safety?
            .map {
                when {
                    it.isDirectory() -> directoryUnsafe(it)
                    it.isFile() -> KorioFileWrapper.fileUnsafe(it)
                    else -> throw IllegalArgumentException("Only files and directories should be possible`")
                }
            }
    }

    override suspend fun newFile(path: String): Either<DirectoryError.NewFileError, Path.File> {
        return either {
            val filePath = vfs.get(path)
            if (filePath.exists()) {
                Either.Left(DirectoryError.NewFileError.FileExists(path))
                    .bind<Path.File>()
            }

            // TODO: 30/10/2022 rohdef - ensure file and not directory - how?
            // is that even poossible???
            KorioFileWrapper.file(filePath)
                .mapLeft { TODO("Type issues") }
                .bind()
        }
    }

    companion object {
        suspend fun directory(vfsFile: VfsFile): Either<DirectoryError, Path.Directory> {
            return if (!vfsFile.isDirectory()) {
                Either.Left(DirectoryError.NotADirectory)
            } else {
                Either.Right(KorioDirectoryWrapper(vfsFile))
            }
        }

        suspend fun directoryUnsafe(vfsFile: VfsFile): Path.Directory {
            val directory = directory(vfsFile)
            return when (directory) {
                is Either.Right -> directory.value
                is Either.Left -> throw IllegalArgumentException("Argh")
            }
        }
    }
}