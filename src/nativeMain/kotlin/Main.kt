import configuration.Arguments
import configuration.Configuration
import engine.JobRunnerImplementation
import engine.Runners
import engine.StepRunnerImplementation
import engine.VariableParser
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import plugins.ActionId
import plugins.local.Debug
import plugins.local.UpdateEnvironment
import plugins.remote.InstallRecipeRunner
import plugins.remote.Reboot
import plugins.remote.RunRecipe
import utilities.LinuxSystemUtilities

private val logger = KotlinLogging.logger {}

fun main(cliArguments: Array<String>) {
    val arguments = Arguments.parseArguments(cliArguments)

    logger.info { "Creating dishes on the menu" }

    val configuration = Configuration(arguments)

    val runners = Runners(
        ActionId("debug@v1") to Debug,
        ActionId("updateEnvironment@v1") to UpdateEnvironment,
        ActionId("installRecipeRunner@v1") to InstallRecipeRunner,
        ActionId("runRecipe@v1") to RunRecipe,
        ActionId("reboot@v1") to Reboot(LinuxSystemUtilities()),
    )

    val variableParser = VariableParser()
    val stepRunner = StepRunnerImplementation(configuration.properties, runners, variableParser)
    val jobRunner = JobRunnerImplementation(stepRunner)

    runBlocking {
        // TODO separate into own engine (or JobRunner?)
        // TODO fix ordered map
        configuration.installation.jobs
            .filter { configuration.jobsToRun.accept(it.key) }
            .forEach {
                jobRunner.run(it.value)
            }
    }

    logger.info { "The menu is ready and served - enjoy :)" }
}