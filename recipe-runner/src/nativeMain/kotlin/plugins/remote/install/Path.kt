package plugins.remote.install

import arrow.core.Either

sealed interface Path {
    val absolutePath: String

    interface Directory : Path {
        suspend fun list(): List<Path>

        suspend fun newFile(path: String): Either<DirectoryError.NewFileError, File>
    }

    interface File : Path {
        suspend fun write(text: String)
    }
}

sealed interface PathError {
}
sealed interface DirectoryError : PathError {
    object NotADirectory : DirectoryError

    sealed interface NewFileError : DirectoryError {
        data class FileExists(val path: String) : NewFileError
    }
}
sealed interface FileError : PathError {
    object NotAFile : FileError
}