package engine

import arrow.core.Either
import arrow.core.flatMap
import configuration.installation.Job
import mu.KotlinLogging

class JobRunnerImplementation(
    private val stepRunner: StepRunner,
) : JobRunner {
    private val logger = KotlinLogging.logger {}

    override suspend fun run(job: Job, initialEnvironment: Environment): Either<EngineError, Environment> {
        logger.info { "Running job: ${job.name}" }

        val initial: Either<EngineError, Environment> = Either.Right(initialEnvironment)
        val finalEnvironment = job.steps.fold(initial) { environment, step ->
            environment.flatMap { env ->
                stepRunner.runStep(step, env).map { updates ->
                    env + updates
                }
            }
        }

        when (finalEnvironment) {
            is Either.Left -> logger.error { "Could not perform all jobs: ${finalEnvironment.value}" }
            is Either.Right -> logger.info { "Successfully ran: ${job.name}" }
        }

        return finalEnvironment
    }
}