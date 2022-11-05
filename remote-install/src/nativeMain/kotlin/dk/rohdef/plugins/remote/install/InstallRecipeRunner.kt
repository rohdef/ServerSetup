package dk.rohdef.plugins.remote.install

import arrow.core.Either
import arrow.core.computations.either
import configuration.Parameters
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction
import dk.rohdef.plugins.remote.executeSshCommand
import dk.rohdef.plugins.remote.scpToRemote
import dk.rohdef.rfpath.PathUtility
import engine.EnvironmentUpdates
import mu.KotlinLogging
import utilities.SystemUtilities

class InstallRecipeRunner(
    private val system: SystemUtilities,
    private val pathUtility: PathUtility,
) : StepAction {
    private val logger = KotlinLogging.logger {}

    override val actionId = ActionId("installRecipeRunner@v1")

    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<InstallError, EnvironmentUpdates> {
        logger.info { "Installer in action" }

        return either {
            val applicationDirectory = pathUtility.applicationDirectory()
            logger.info { applicationDirectory.absolutePath }
            logger.info { applicationDirectory.absolutePath }

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
                    applicationDirectory.list(),
                    "/tmp/gourmet-runner",
                )
                .mapLeft { InstallError.CannotExecuteCommand(it) }
                .bind()



            logger.info { "Askpass" }
            val askpassFile = pathUtility.createTemporaryFile()
                // TODO: 05/11/2022 rohdef - better error handling
                .mapLeft { InstallError.CannotWriteAskpassFile }
                .bind()
            askpassFile.write(
                """
                #!/usr/bin/env bash

                echo "hahaha!"
            """.trimIndent()
            )
                // TODO: 05/11/2022 rohdef - better error handling
                .mapLeft { InstallError.CannotWriteAskpassFile }
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