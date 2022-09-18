package plugins.remote.install

import arrow.core.Either
import configuration.Parameters
import engine.EngineError
import engine.EnvironmentUpdates
import plugins.StepAction
import plugins.remote.Host
import plugins.remote.executeSshCommand
import utilities.SystemUtilities

class InstallRecipeRunner(
    private val system: SystemUtilities,
) : StepAction {
    override suspend fun run(
        parameters: Parameters.Map,
    ): Either<EngineError, EnvironmentUpdates> {
        val resultA = system.executeSshCommand(
            Host(
                "configuration.host",
                22,
                "ubuntu",
                "ubuntu",
            ),
            "rm", "-rf", "/tmp/gourmet-askpass"
        )
        val resultD = system.executeSshCommand(
            Host(
                "configuration.host",
                22,
                "ubuntu",
                "ubuntu",
            ),
            "rm", "-rf", "/tmp/gourmet-data"
        )
        val resultR = system.executeSshCommand(
            Host(
                "configuration.host",
                22,
                "ubuntu",
                "ubuntu",
            ),
            "rm", "-rf", "/tmp/gourmet-runner"
        )

        TODO("not implemented")
    }


}