import com.soywiz.korio.file.std.applicationVfs
import com.soywiz.korio.file.std.cwdVfs
import configuration.Arguments
import configuration.Configuration
import engine.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import plugins.StepAction
import plugins.local.Debug
import plugins.local.UpdateEnvironment
import plugins.remote.RunRecipe
import plugins.remote.install.ApplicationPath
import plugins.remote.install.InstallRecipeRunner
import plugins.remote.install.WorkDirectoryPath
import plugins.remote.reboot.KtorSockets
import plugins.remote.reboot.Reboot
import plugins.remote.reboot.SocketFactory
import utilities.LinuxSystemUtilities
import utilities.SystemUtilities

private val logger = KotlinLogging.logger {}

fun main(cliArguments: Array<String>) = runBlocking {
    logger.info { "Creating dishes on the menu" }

    val configurationModule = module {
        single { Arguments.parseArguments(cliArguments) }
        single { Configuration(get()) }
    }

    val systemModule = module {
        single { WorkDirectoryPath(cwdVfs) }
        single { ApplicationPath(applicationVfs) }
        singleOf(::LinuxSystemUtilities) bind SystemUtilities::class
        // TODO exterminate if korio can replace - better interface ;)
        singleOf(::KtorSockets) bind SocketFactory::class
    }

    val runnersModule = module {
        singleOf(::Debug) bind StepAction::class
        singleOf(::InstallRecipeRunner) bind StepAction::class
        singleOf(::Reboot) bind StepAction::class
        singleOf(::RunRecipe) bind StepAction::class
        singleOf(::UpdateEnvironment) bind StepAction::class

        single { Runners(getAll()) }
    }

    val runnerModule = module {
        singleOf(::VariableParser)
        singleOf(::StepRunnerImplementation) bind StepRunner::class
        singleOf(::JobRunnerImplementation) bind JobRunner::class
    }

    startKoin {
        modules(
            configurationModule,
            systemModule,
            runnersModule,
            runnerModule,
        )
    }

    val application = RecipeApplication()
    application.main()

    logger.info { "The menu is ready and served - enjoy :)" }
}

private class RecipeApplication : KoinComponent {
    private val jobRunner: JobRunner by inject()
    private val configuration: Configuration by inject()

    suspend fun main() {
        // TODO fix ordered map
        configuration.installation.jobs
            .filter { configuration.jobsToRun.accept(it.key) }
            .forEach {
                jobRunner.run(it.value)
            }

        logger.info { "The menu is ready and served - enjoy :)" }
    }
}