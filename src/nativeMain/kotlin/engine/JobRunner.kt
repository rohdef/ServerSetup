package engine

import configuration.installation.Job
import it.czerwinski.kotlin.util.Either

interface JobRunner {
    fun run(job: Job, initialEnvironment: Environment = emptyMap()): Either<EngineError, Environment>
}