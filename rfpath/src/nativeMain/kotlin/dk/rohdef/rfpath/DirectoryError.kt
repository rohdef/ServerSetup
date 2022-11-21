package dk.rohdef.rfpath

sealed interface DirectoryError : PathError

sealed interface NewFileError : DirectoryError {
    data class FileExists(val path: String) : NewFileError
}

sealed interface DirectoryInstance : DirectoryError {
    data class EntityIsAFile(val path: String) : DirectoryInstance
    data class EntityIsNonExisting(val path: String) : DirectoryInstance
}