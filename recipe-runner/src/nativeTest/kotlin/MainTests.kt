import arrow.core.Either
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

class MainTests {
    private val runLog = DummyRunner()
    private val generateScriptLog = DummyRunner()

    private val commandMapping = mapOf(
        Command.RUN to runLog::runCommand,
        Command.GENERATE_SCRIPT to generateScriptLog::runCommand,
    )

    private fun makeTestRun(vararg parameters: String): Either<Any, Unit> =
        actualMain(commandMapping, arrayOf(*parameters))

    @Test
    fun `run command`() {
        makeTestRun(
            "run",
        )

        runLog.runs shouldContainExactly listOf(
            emptyList(),
        )
        generateScriptLog.runs shouldContainExactly emptyList()
    }

    @Test
    fun `generate script command`() {
        makeTestRun(
            "generate-script",
        )

        runLog.runs shouldContainExactly emptyList()
        generateScriptLog.runs shouldContainExactly listOf(
            emptyList(),
        )
    }

    @Test
    fun `commands are case insensitie`() {
        makeTestRun(
            "Generate-Script",
            "capitalized",
        )
        makeTestRun(
            "GENERATE-SCRIPT",
            "all capital",
        )
        makeTestRun(
            "gEneRATe-ScrIPt",
            "shuffled",
        )

        runLog.runs shouldContainExactly emptyList()
        generateScriptLog.runs shouldContainExactly listOf(
            listOf("capitalized"),
            listOf("all capital"),
            listOf("shuffled"),
        )
    }

    @Test
    fun `commands use dash for separator`() {
        makeTestRun(
            "generate-script",
            "dash separator",
        )
        makeTestRun(
            "generatescript",
            "no separator",
        )
        makeTestRun(
            "generate script",
            "space separator",
        )
        makeTestRun(
            "generate_script",
            "underscore separator",
        )

        runLog.runs shouldContainExactly emptyList()
        generateScriptLog.runs shouldContainExactly listOf(
            listOf("dash separator"),
            listOf("underscore separator"),
        )
    }

    @Test
    fun `arguments are passed on`() {
        makeTestRun(
            "run",
            "foo",
            "bar",
        )
        makeTestRun(
            "run",
            "blib",
            "blob",
        )
        makeTestRun(
            "run",
            "something with space",
            "42",
            "some=13"
        )

        runLog.runs shouldContainExactly listOf(
            listOf("foo", "bar"),
            listOf("blib", "blob"),
            listOf("something with space", "42", "some=13"),
        )
        generateScriptLog.runs shouldContainExactly emptyList()
    }

    private class DummyRunner(
    ) {
        val runs: MutableList<List<String>> = mutableListOf()

        suspend fun runCommand(cliArguments: Array<String>) {
            runs.add(listOf(*cliArguments))
        }
    }
}