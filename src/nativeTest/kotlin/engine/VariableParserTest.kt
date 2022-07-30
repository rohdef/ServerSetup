package engine

import configuration.Parameters
import engine.VariableParser
import engine.VariableParserError
import io.kotest.matchers.shouldBe
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import kotlin.test.Test

class VariableParserTest {
    private val parser = VariableParser()

    @Test
    fun `no variable should return itself`() {
        val testValue = "simple string"

        val properties = mapOf(
            "hostname" to "door-mat.co.uk"
        )
        val environment = mapOf(
            "sshUsername" to "ubuntu"
        )

        val newParameter = parser.parse(properties, environment, testValue)
        val expectedValue: Either<VariableParserError, String> =
            Right(testValue)
        newParameter.shouldBe(expectedValue)
    }

    @Test
    fun `simple variable`() {
        val testFromProperties = "lindy-town.de"
        val testFromEnvironment = "camilla"
        val properties = mapOf(
            "hostname" to testFromProperties
        )
        val environment = mapOf(
            "user" to testFromEnvironment
        )

        val parameterFromProperties = parser.parse(properties, environment, "\$properties.hostname")
        val expectedPropertiesValue: Either<VariableParserError, String> =
            Right(testFromProperties)
        parameterFromProperties.shouldBe(expectedPropertiesValue)

        val parameterFromEnvironment = parser.parse(properties, environment, "\$environment.user")
        val expectedEnvironmentValue: Either<VariableParserError, String> =
            Right(testFromEnvironment)
        parameterFromEnvironment.shouldBe(expectedEnvironmentValue)
    }

    @Test
    fun `empty data sets`() {
        val properties = emptyMap<String, String>()
        val environment = emptyMap<String, String>()

        val parameterFromProperties = parser.parse(properties, environment, "\$properties.hostname")
        val expectedFromProperties: Either<VariableParserError, String> =
            Left(VariableParserError.VariableNotFound("\$properties.hostname"))
        parameterFromProperties.shouldBe(expectedFromProperties)

        val parameterFromEnvironment = parser.parse(properties, environment, "\$environment.user")
        val expectedFromEnvironment: Either<VariableParserError, String> =
            Left(VariableParserError.VariableNotFound("\$environment.user"))
        parameterFromEnvironment.shouldBe(expectedFromEnvironment)
    }

    @Test
    fun `recursion through environment is not possible`() {
        val testValue = "\$environment.destination"

        val properties = mapOf(
            "hostname" to "dancing-swing.com"
        )
        val environment = mapOf(
            "destinatation" to "\$environment.hostname",
            "hostname" to testValue
        )
        val text = "\$environment.hostname"

        val newParameter = parser.parse(properties, environment, text)
        val expectedParameter: Either<VariableParserError, String> =
            Right(
                testValue
            )
        newParameter.shouldBe(expectedParameter)
    }

    @Test
    fun `Parameter map parsing`() {
        val properties = mapOf(
            "hostname" to "funky-town.de"
        )
        val environment = mapOf(
            "user" to "camilla",
            "host" to "dancing-queen.dk"
        )
        val parameters = Parameters.Map(
            "simple" to Parameters.String("\$environment.host"),
            "hide it in a list" to Parameters.List(
                Parameters.String("\$properties.hostname")
            ),
            "hide it in a map" to Parameters.Map(
                "funky" to Parameters.String("\$environment.user")
            ),
        )

        val newMap = parser.parse(properties, environment, parameters)
        val expectedMap = Right(
            Parameters.Map(
                "simple" to Parameters.String("dancing-queen.dk"),
                "hide it in a list" to Parameters.List(
                    Parameters.String("funky-town.de"),
                ),
                "hide it in a map" to Parameters.Map(
                    "funky" to Parameters.String("camilla"),
                ),
            )
        )
        newMap.shouldBe(expectedMap)
    }

    @Test
    fun `Parameter map with missing variable`() {
        val properties = mapOf<String, String>()
        val environment = mapOf<String, String>()
        val parameters = Parameters.Map(
            "simple" to Parameters.String("\$environment.host"),
        )

        val newMap = parser.parse(properties, environment, parameters)
        val expectedMap = Left(VariableParserError.VariableNotFound("\$environment.host"))
        newMap.shouldBe(expectedMap)
    }
}