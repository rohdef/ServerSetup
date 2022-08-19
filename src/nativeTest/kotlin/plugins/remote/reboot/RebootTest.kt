package plugins.remote.reboot

import arrow.core.Either
import configuration.ParameterError
import configuration.Parameters
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.network.sockets.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.utilities.TestSystemUtilities
import utilities.SystemUtilityError
import kotlin.test.Ignore
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
        val configuration = Configuration.create(parameters)

        configuration.shouldBe(
            Either.Right(
                Configuration(
                    Configuration.Host(
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
        val configuration = Configuration.create(parameters)

        configuration.shouldBe(
            Either.Right(
                Configuration(
                    Configuration.Host(
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

    @Test
    fun `Bad configuration`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "password" to Parameters.String("somePassw0rd"),
            ),
        )
        val configuration = Configuration.create(parameters)

        val expected = Either.Left(
            ParameterError.UnknownKey(
                "username",
            )
        )
        configuration.shouldBe(expected)
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

        val expectedAddress = InetSocketAddress("rebootable.local", 22)
        socketFactory.generatedSockets.keys
            .shouldContainExactly(setOf(expectedAddress))

        val sockets = socketFactory.generatedSockets[expectedAddress]!!
        sockets.shouldHaveSize(1)
    }

    @Test
    fun `Do not attempt to connect if not waiting`() = runTest {
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

        socketFactory.generatedSockets.shouldBeEmpty()
    }

    @Test
    @Ignore
    fun `Give up after 30 connection attempts`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
            ),
            "username" to Parameters.String("myuser"),
            "waitForReboot" to Parameters.String("WAIT"),
        )

        TODO("Not currently possible, feature is not supported by ktor yet: https://youtrack.jetbrains.com/issue/KTOR-4728")
    }
}