package engine

import arrow.core.Either
import arrow.core.flatMap
import configuration.installation.Step
import mu.KotlinLogging

class StepRunnerImplementation(
    private val properties: Properties,
    private val runners: Runners,
    private val parser: VariableParser
) : StepRunner {
    private val logger = KotlinLogging.logger {}

    override suspend fun run(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates> {
        logger.info { "Running step: ${step.name}" }

        return runners.runners.getValue(step.uses)
            .flatMap { runner ->
                parser.parse(properties, environment, step.parameters)
                    .flatMap { runner.run(it) }
            }
    }
}