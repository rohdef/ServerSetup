package engine

import arrow.core.Either
import configuration.installation.Job

interface JobRunner {
    suspend fun run(job: Job, initialEnvironment: Environment = emptyMap()): Either<EngineError, Environment>
}