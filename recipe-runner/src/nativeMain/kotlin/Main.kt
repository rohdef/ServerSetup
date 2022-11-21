import arrow.core.getOrElse
import configuration.Arguments
import configuration.Configuration
import dk.rohdef.plugins.StepAction
import dk.rohdef.plugins.debug.Debug
import dk.rohdef.plugins.local.UpdateEnvironment
import dk.rohdef.plugins.remote.RunRecipe
import dk.rohdef.plugins.remote.install.InstallRecipeRunner
import dk.rohdef.plugins.remote.reboot.KtorSockets
import dk.rohdef.plugins.remote.reboot.Reboot
import dk.rohdef.plugins.remote.reboot.SocketFactory
import dk.rohdef.rfpath.okio.OkioPathUtility
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.UserGroup
import dk.rohdef.rfpath.utility.PathUtility
import engine.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import utilities.LinuxSystemUtilities
import utilities.SystemUtilities

private val logger = KotlinLogging.logger {}

fun main(cliArguments: Array<String>) = runBlocking {
    logger.info { "Creating dishes on the menu" }

    val configurationModule = module {
        single { Arguments.parseArguments(cliArguments) }
        single { Configuration(get()) }
    }

    val pathUtility = OkioPathUtility.createPathUtilityUnsafe()
    val systemModule = module {
        single { pathUtility }
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
    private val pathUtility: PathUtility by inject()

    suspend fun main() {
        // TODO fix ordered map
        val w = pathUtility.workDirectory().getOrElse { TODO() }
        val t = pathUtility.createTemporaryFile().getOrElse { TODO() }

        println(w.absolutePath)

        println(t.absolutePath)
        t.write("Hello world")
        t.addPermission(UserGroup.OWNER, Permission.EXECUTE)

//        configuration.installation.jobs
//            .filter { configuration.jobsToRun.accept(it.key) }
//            .forEach {
//                jobRunner.run(it.value)
//            }
    }
}