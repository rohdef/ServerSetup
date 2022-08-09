package plugins.remote.reboot

import arrow.core.Either
import configuration.Parameters
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.utilities.TestSystemUtilities
import utilities.SystemUtilityError
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RebootTest {
    private val socketFactory = TestSockets()
    private val system = TestSystemUtilities()
    private val reboot = Reboot(system, socketFactory)

    init {
        system.nextResult = Either.Left(
            SystemUtilityError.ErrorRunningCommand(
                "",
                1,
                "Connection closed by remote host",
            )
        )
    }

    @Test
    fun `Parameters to configuration`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "port" to Parameters.Integer(43),
                "username" to Parameters.String("myuser"),
                "password" to Parameters.String("somePassw0rd"),
            ),
            "waitForReboot" to Parameters.String("WAIT"),
            "scriptPath" to Parameters.String("server-scripts"),
        )
        val configuration = Reboot.Configuration.create(parameters)

        configuration.shouldBe(
            Either.Right(
                Reboot.Configuration(
                    Reboot.Configuration.Host(
                        "rebootable.local",
                        43,
                        "myuser",
                        "somePassw0rd"
                    ),
                    Reboot.WaitForReboot.WAIT,
                    "server-scripts",
                )
            )
        )
    }

    @Test
    fun `Minimal configuration`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "username" to Parameters.String("myuser"),
                "password" to Parameters.String("somePassw0rd"),
            ),
        )
        val configuration = Reboot.Configuration.create(parameters)

        configuration.shouldBe(
            Either.Right(
                Reboot.Configuration(
                    Reboot.Configuration.Host(
                        "rebootable.local",
                        22,
                        "myuser",
                        "somePassw0rd"
                    ),
                    Reboot.WaitForReboot.DO_NOT_WAIT,
                    "do-configure",
                )
            )
        )
    }

    fun `Bad configuration`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "password" to Parameters.String("somePassw0rd"),
            ),
        )
        val configuration = Reboot.Configuration.create(parameters)

        TODO()
    }

    @Test
    fun `Run reboot command`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "username" to Parameters.String("myuser"),
                "password" to Parameters.String("somePassw0rd"),
            ),
            "waitForReboot" to Parameters.String("DO_NOT_WAIT"),
        )

        val result = reboot.run(parameters)
        result.shouldBeInstanceOf<Either.Right<*>>()

        val expectedCommand = "shutdown"
        val expectedParameterReboot = "-r"
        val expectedParameterNow = "now"

        val relevantExecutions = system.executions
            .filter { it.command.contains(expectedCommand) }
        relevantExecutions.shouldHaveSize(1)

        val relevantCommand = relevantExecutions.first().command
        relevantCommand.shouldContain(expectedParameterReboot)
        relevantCommand.shouldContain(expectedParameterNow)
    }

    @Test
    fun `Attempt to connect if wait`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "username" to Parameters.String("myuser"),
                "password" to Parameters.String("somePassw0rd"),
            ),
            "waitForReboot" to Parameters.String("WAIT"),
        )

        val result = reboot.run(parameters)
        result.shouldBeInstanceOf<Either.Right<*>>()
    }

    @Test
    fun `Do not attempt to connect if not waiting`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
            ),
            "username" to Parameters.String("myuser"),
            "waitForReboot" to Parameters.String("DO_NOT_WAIT"),
        )

        TODO()
    }

    @Test
    fun `Give up after 30 connection attempts`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
            ),
            "username" to Parameters.String("myuser"),
            "waitForReboot" to Parameters.String("WAIT"),
        )

        TODO()
    }
}