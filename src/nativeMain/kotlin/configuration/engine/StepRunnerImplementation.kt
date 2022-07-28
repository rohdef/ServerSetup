package configuration.engine

import configuration.installation.Step
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.flatMap
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class StepRunnerImplementation(
    private val properties: Properties,
    private val runners: Runners,
    private val parser: VariableParser
) : StepRunner {
    override fun run(step: Step, environment: Environment): Either<EngineError, EnvironmentUpdates> {
        logger.info { "Running step: ${step.name}" }

        return runners.runners.getValue(step.uses)
            .flatMap { runner ->
                parser.parse(properties, environment, step.parameters)
                    .flatMap { runner.run(it) }
            }
    }
}