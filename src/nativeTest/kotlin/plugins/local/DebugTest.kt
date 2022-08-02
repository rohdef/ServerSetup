package plugins.local

import arrow.core.Either
import configuration.Parameters
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mu.Appender
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
class DebugTest {
    private val appender = CacheAppender()

    @BeforeTest
    fun `set appender`() {
        mu.KotlinLoggingConfiguration
            .appender = appender
    }

    private val parameters = Parameters.Map(
        "text" to Parameters.String("some text"),
        "integer" to Parameters.Integer(30),
        "list" to Parameters.List(
            Parameters.String("list item"),
            Parameters.Integer(4),
        ),
        "map" to Parameters.Map(
            "another text" to Parameters.String("map item"),
            "another integer" to Parameters.Integer(9),
        ),
    )

    @Test
    fun `Doesn't update map`() = runTest {
        val result = Debug.run(parameters)

        result.shouldBe(Either.Right(emptyMap()))
    }

    @Test
    fun `Outputs parameters`() = runTest {
        Debug.run(parameters)

        val log = appender.messages.joinToString("\n")

        checkContents(log, parameters)
    }

    private fun checkContents(log: String, parameters: Parameters) {
        when (parameters) {
            is Parameters.Integer -> log.shouldContain(parameters.value.toString())
            is Parameters.List ->
                parameters.value.forAll {
                    checkContents(log, it)
                }
            is Parameters.Map -> {
                parameters.value.forAll {
                    log.shouldContain(it.key)
                    checkContents(log, it.value)
                }
            }
            is Parameters.String -> log.shouldContain(parameters.value)
        }
    }
}

private class CacheAppender : Appender {
    private val _messages = mutableListOf<String>()
    val messages: List<String>
        get() = _messages.toList()

    override val includePrefix: Boolean
        get() = false

    override fun debug(loggerName: String, message: String) {
        _messages.add(message)
    }

    override fun error(loggerName: String, message: String) {
        _messages.add(message)
    }

    override fun info(loggerName: String, message: String) {
        _messages.add(message)
    }

    override fun trace(loggerName: String, message: String) {
        _messages.add(message)
    }

    override fun warn(loggerName: String, message: String) {
        _messages.add(message)
    }
}