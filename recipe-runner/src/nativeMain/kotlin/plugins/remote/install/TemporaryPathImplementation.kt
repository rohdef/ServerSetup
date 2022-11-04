package plugins.remote.install

import arrow.core.Either
import arrow.core.computations.either
import com.soywiz.korio.util.UUID

value class TemporaryPathImplementation(
    private val directory: Path.Directory,
) : Path.Directory by directory, TemporaryPath {
    override suspend fun newTemporaryFile(): Either<PathError, Path.File> {
        return either {
            val uuid = UUID.randomUUID().toString()
            // TODO: 29/10/2022 rohdef - get actual app name
            val applicationName = "gourmet"

            directory
                .newFile("${applicationName}-${uuid}")
                .bind()
        }
    }
}