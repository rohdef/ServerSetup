package plugins.remote

import utilities.SystemUtilities
import kotlin.test.Test

class RebootTest {
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