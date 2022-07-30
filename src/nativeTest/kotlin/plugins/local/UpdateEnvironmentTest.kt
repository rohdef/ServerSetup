package plugins.local

import configuration.Parameters
import plugins.local.UpdateEnvironment
import plugins.local.UpdateEnvironmentError
import io.kotest.matchers.shouldBe
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import kotlin.test.Test

class UpdateEnvironmentTest {
    @Test
    fun `Sends step parameters as environment updates`() {
        val parameters = Parameters.Map(
            "something" to Parameters.String("else"),
            "enable" to Parameters.Boolean(true),
            "issues" to Parameters.Integer(37),
        )

        val result = UpdateEnvironment.run(parameters)

        val expected = Right(
            mapOf(
                "something" to "else",
                "enable" to "true",
                "issues" to "37",
            )
        )
        result.shouldBe(expected)
    }

    @Test
    fun `Does not allow maps`() {
        val parameters = Parameters.Map(
            "parameters" to Parameters.Map(
                "age" to Parameters.String("37"),
                "person" to Parameters.String("Bastard"),
            ),
        )

        val result = UpdateEnvironment.run(parameters)

        val expected = Left(UpdateEnvironmentError.MapNotAllowed("parameters"))
        result.shouldBe(expected)
    }

    @Test
    fun `Does not allow lists`() {
        val parameters = Parameters.Map(
            "something" to Parameters.List(
                Parameters.String("Postman"),
                Parameters.String("Milkman"),
            ),
        )

        val result = UpdateEnvironment.run(parameters)

        val expected = Left(UpdateEnvironmentError.ListNotAllowed("something"))
        result.shouldBe(expected)
    }
}