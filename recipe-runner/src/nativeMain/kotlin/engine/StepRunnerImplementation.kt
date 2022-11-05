package engine

import arrow.core.Either
import arrow.core.computations.either
import configuration.Configuration
import configuration.installation.Step
import mu.KotlinLogging

class StepRunnerImplementation(
    private val runners: Runners,
    private val parser: VariableParser,
    private val configuration: Configuration,
) : StepRunner {
    private val logger = KotlinLogging.logger {}

    override suspend fun runStep(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates> {
        logger.info { "Running step: ${step.name}" }

        return either {
            val runner = runners.runners.getValue(step.uses).bind()
            val parameters = parser.parse(
                configuration.properties,
                environment,
                step.parameters
            ).bind()

            runner.run(parameters).bind()
        }
    }
}