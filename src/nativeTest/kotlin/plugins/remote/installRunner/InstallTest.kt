package plugins.remote.installRunner

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.utilities.TestSystemUtilities
import kotlin.test.Ignore
import kotlin.test.Test

@ExperimentalCoroutinesApi
class InstallTest {
    private val system = TestSystemUtilities()

    // TODO: 18/09/2022 rohdef - look into weird import :/
//    private val install = plugins.remote.InstallRecipeRunner(system)

    @Test
    @Ignore
    fun `Createes files and environment`() = runTest {
        val username = "user"
        val password = "pas"
        val hostname = "domain"
        val port = 22
        val applicationPath = "/tmp/gourmet-application"
        val dataPath = "/tmp/gourmet-data"
        val askpassPath = "/tmp/gourmet-askpass"

        system.executions.shouldHaveSize(4)
        val execution1 = system.executions[0]
        val execution2 = system.executions[1]
        val execution3 = system.executions[2]
        val execution4 = system.executions[3]
        val execution5 = system.executions[4]
        val execution6 = system.executions[5]

        // clean system
        execution1.command.shouldContain("ssh \"$username@$hostname\" \"-p\" \"$port\" \"rm\" \"-rf\" \"$dataPath\"")
        execution2.command.shouldContain("ssh \"$username@$hostname\" \"-p\" \"$port\" \"rm\" \"-rf\" \"$applicationPath\"")
        execution3.command.shouldContain("ssh \"$username@$hostname\" \"-p\" \"$port\" \"rm\" \"-rf\" \"$askpassPath\"")

        // create askpass
        // TODO: 16/09/2022 rohdef - read file
        val passwordFile = "futte"
        passwordFile.shouldBe(password)

        // TODO: 16/09/2022 rohdef - deal with local paths
        // install askpass
        execution4.command.shouldContain("scp \"-r\" \"...\" \"$username@$hostname:$port$askpassPath\"")

        // install app
        execution5.command.shouldContain("scp \"-r\" \"...\" \"$username@$hostname:$port$applicationPath\"")

        // install data
        execution6.command.shouldContain("scp \"-r\" \"...\" \"$username@$hostname:$port$dataPath\"")
    }
}