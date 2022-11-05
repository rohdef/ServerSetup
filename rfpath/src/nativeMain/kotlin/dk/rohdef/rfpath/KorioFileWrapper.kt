package dk.rohdef.rfpath

import arrow.core.Either
import arrow.core.right
import com.soywiz.korio.file.VfsFile

class KorioFileWrapper private constructor(
    private val vfs: VfsFile,
) : Path.File {
    override val absolutePath = vfs.absolutePath

    override suspend fun write(text: String): Either<FileError, Unit> {
        vfs.writeString(text)
        return Unit.right()
    }

    companion object {
        suspend fun file(vfsFile: VfsFile): Either<FileError, Path.File> {
            return if (!vfsFile.isFile()) {
                Either.Left(FileError.NotAFile)
            } else {
                Either.Right(KorioFileWrapper(vfsFile))
            }
        }

        suspend fun fileUnsafe(vfsFile: VfsFile): Path.File {
            val file = file(vfsFile)
            return when(file) {
                is Either.Right -> file.value
                is Either.Left -> throw IllegalArgumentException("Argh")
            }
        }
    }
}