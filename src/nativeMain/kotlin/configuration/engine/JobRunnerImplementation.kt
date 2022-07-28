package configuration.engine

import configuration.installation.Job
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class JobRunnerImplementation(
    private val stepRunner: StepRunner,
) : JobRunner {
    override fun run(job: Job, initialEnvironment: Environment): Either<EngineError, Environment> {
        logger.info { "Running job: ${job.name}" }

        val initial: Either<EngineError, Environment> = Right(initialEnvironment)
        val finalEnvironment = job.steps.fold(initial) { environment, step ->
            environment.flatMap { env ->
                stepRunner.run(step, env).map { updates ->
                    env + updates
                }
            }
        }

        when (finalEnvironment) {
            is Left -> logger.error { "Could not perform all jobs: ${finalEnvironment.value}" }
            is Right -> logger.info { "Successfully ran: ${job.name}" }
        }

        return finalEnvironment
    }
}