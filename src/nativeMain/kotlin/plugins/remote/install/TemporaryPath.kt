package plugins.remote.install

import arrow.core.Either

interface TemporaryPath : Path.Directory {
    suspend fun newTemporaryFile(): Either<PathError, Path.File>
}