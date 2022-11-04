package plugins.remote.install

import arrow.core.Either
import arrow.core.computations.either
import configuration.Parameters
import engine.EnvironmentUpdates
import mu.KotlinLogging
import plugins.ActionId
import plugins.StepAction
import plugins.remote.executeSshCommand
import plugins.remote.scpToRemote
import utilities.SystemUtilities

class InstallRecipeRunner(
    private val system: SystemUtilities,
    private val applicationPath: ApplicationPath,
    private val workDirectoryPath: WorkDirectoryPath,
) : StepAction {
    private val logger = KotlinLogging.logger {}

    override val actionId = ActionId("installRecipeRunner@v1")

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<InstallError, EnvironmentUpdates> {
        logger.info { "Installer in action" }

        return either {
            logger.info { applicationPath.absolutePath }
            logger.info { workDirectoryPath.absolutePath }

            val configuration = Configuration.create(parameters)
                .mapLeft { InstallError.CouldNotParseConfiguration(it) }
                .bind()

            system
                .executeSshCommand(
                    configuration.host,
                    "rm", "-rf", "/tmp/gourmet-runner/"
                )
                .mapLeft { InstallError.CannotExecuteCommand(it) }
                .bind()
            system
                .executeSshCommand(
                    configuration.host,
                    "mkdir", "-p", "/tmp/gourmet-runner/"
                )
                .mapLeft { InstallError.CannotExecuteCommand(it) }
                .bind()
            system
                .scpToRemote(
                    configuration.host,
                    applicationPath.list(),
                    "/tmp/gourmet-runner",
                )
                .mapLeft { InstallError.CannotExecuteCommand(it) }
                .bind()



            logger.info { "askpass" }
            system
                .executeSshCommand(
                    configuration.host,
                    "echo", "...", "/tmp/gourmet-runner/askpass.sh"
                )
                .mapLeft { InstallError.CannotExecuteCommand(it) }
                .bind()

            emptyMap()
        }

//        logger.info { "data" }
//        val resultD = system.executeSshCommand(
//            host,
//            "rm", "-rf", "/tmp/gourmet-data"
//        )
    }
}