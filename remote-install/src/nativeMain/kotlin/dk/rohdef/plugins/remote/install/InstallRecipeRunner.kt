package dk.rohdef.plugins.remote.install

import arrow.core.Either
import arrow.core.continuations.either
import configuration.Parameters
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction
import dk.rohdef.plugins.remote.executeSshCommand
import dk.rohdef.plugins.remote.scpToRemote
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.UserGroup
import dk.rohdef.rfpath.utility.PathUtility
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
            val configuration = Configuration.create(parameters)
                .mapLeft { InstallError.CouldNotParseConfiguration(it) }
                .bind()

            setupGourmetRunner(configuration)
                .bind()

            logger.info { "data" }
            val workDirectory = pathUtility.workDirectory()
                .mapLeft { ApplicationDirectoryUnavailable.fromDirectoryInstance(it) }
                .bind()
            val grd = workDirectory

//        val resultD = system.executeSshCommand(
//            host,
//            "rm", "-rf", "/tmp/gourmet-data"
//        )

            emptyMap()
        }
    }

    private suspend fun setupGourmetRunner(
        configuration: Configuration,
    ): Either<InstallError, Unit> = either {
        val applicationDirectory = pathUtility.applicationDirectory()
            .mapLeft { ApplicationDirectoryUnavailable.fromDirectoryInstance(it) }
            .bind()

        val homeDirectory = "/home/${configuration.host.username}/"
        val runnerPath = "${homeDirectory}gourmet-runner/"

        logger.info { "Gourmet sources" }
        val gourmetRunnerSources = applicationDirectory.list()
            .mapLeft { InstallError.CannotReadGourmetRunner(applicationDirectory) }
            .bind()

        logger.info { "Askpass" }
        val askpassFile = createAskPassFile(configuration)
            .bind()

        logger.info { "Copying installation" }
        setupRemoteDirectory(configuration, gourmetRunnerSources, runnerPath)
            .bind()
        copyToRemote(configuration, listOf(askpassFile), runnerPath + "ask-pass.sh")
            .bind()
    }

    private suspend fun createAskPassFile(
        configuration: Configuration,
    ): Either<InstallError, Path.File> = either {
        val askpassFile = pathUtility.createTemporaryFile()
            // TODO: 05/11/2022 rohdef - better error handling
            .mapLeft { InstallError.CannotWriteAskpassFile }
            .bind()
        askpassFile
            .write(
                """
                #!/usr/bin/env bash

                echo "${configuration.host.password}"
                """.trimIndent()
            )
            .mapLeft { InstallError.CannotWriteAskpassFile }
            .bind()
            .addPermission(
                UserGroup.OWNER, Permission.EXECUTE
            )

        askpassFile
    }

    private suspend fun setupRemoteDirectory(
        configuration: Configuration,
        sources: List<Path<*>>,
        destinationPath: String,
    ): Either<InstallError, String> = either {
        runRemotely(configuration, "rm", "-rf", destinationPath)
            .bind()
        runRemotely(configuration, "mkdir", "-p", destinationPath)
            .bind()

        copyToRemote(configuration, sources, destinationPath)
            .bind()
    }

    private fun copyToRemote(
        configuration: Configuration,
        sources: List<Path<*>>,
        remotePath: String
    ): Either<InstallError, String> {
        return system
            .scpToRemote(
                configuration.host,
                sources,
                remotePath,
            )
            .mapLeft { InstallError.CannotExecuteRemoteCommand(it) }
    }

    private fun runRemotely(
        configuration: Configuration,
        executable: String,
        vararg paramaters: String,
    ): Either<InstallError, String> {
        return system
            .executeSshCommand(
                configuration.host,
                executable,
                *paramaters,
            )
            .mapLeft { InstallError.CannotExecuteRemoteCommand(it) }
    }
}