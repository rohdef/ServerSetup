package plugins.remote

import arrow.core.Either
import configuration.Parameters
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import utilities.SystemUtilities
import kotlin.test.Test

@ExperimentalCoroutinesApi
class RebootTest {
    @Test
    fun `Parameters to configuration`() = runTest {
        val parameters = Parameters.Map(
            "host" to Parameters.Map(
                "hostname" to Parameters.String("rebootable.local"),
                "port" to Parameters.Integer(43),
            ),
            "username" to Parameters.String("myuser"),
            "waitForReboot" to Parameters.String("WAIT"),
            "scriptPath" to Parameters.String("server-scripts"),
        )
        val configuration = Reboot.Configuration.create(parameters)

        configuration.shouldBe(
            Either.Right(
                Reboot.Configuration(
                    Reboot.Configuration.Host(
                        "rebootable.local",
                        43
                    ),
                    "myuser",
                    Reboot.WaitForReboot.WAIT,
                    "server-scripts",
                )
            )
        )
    }

    @Test
    fun `executing commands`() {
        val utilities = SystemUtilities()
        val hmm = utilities.executeCommand(
            "touch",
            "/tmp/rohdef",
        )
        println("Ran: ")
        println(hmm)

        val hm = utilities.executeCommand("export BLAH=\"John\"; echo \"hi there\${BLAH}\" >> /tmp/rohdef")
        println("Ran: ")
        println(hm)

        val h = utilities.executeCommand("cat", "/tmp/rohdef")
        println("read")
        println(h)
    }
}