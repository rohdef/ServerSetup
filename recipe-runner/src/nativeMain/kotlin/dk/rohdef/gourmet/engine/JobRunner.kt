package dk.rohdef.gourmet.engine

import arrow.core.Either
import configuration.installation.Job
import engine.EngineError

interface JobRunner {
    suspend fun run(job: Job, initialEnvironment: Environment = emptyMap()): Either<EngineError, Environment>
}