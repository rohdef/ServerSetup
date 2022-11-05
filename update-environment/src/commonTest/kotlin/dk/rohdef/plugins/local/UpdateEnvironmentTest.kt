package dk.rohdef.plugins.local

import arrow.core.Either
import configuration.Parameters
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
class UpdateEnvironmentTest {
    private val updateEnvironment = UpdateEnvironment()

    @Test
    fun `Sends step parameters as environment updates`() = runTest {
        val parameters = Parameters.Map(
            "something" to Parameters.String("else"),
            "issues" to Parameters.Integer(37),
        )

        val result = updateEnvironment.run(parameters)

        val expected = Either.Right(
            mapOf(
                "something" to "else",
                "issues" to "37",
            )
        )
        result.shouldBe(expected)
    }

    @Test
    fun `Does not allow maps`() = runTest {
        val parameters = Parameters.Map(
            "parameters" to Parameters.Map(
                "age" to Parameters.String("37"),
                "person" to Parameters.String("Bastard"),
            ),
        )

        val result = updateEnvironment.run(parameters)

        val expected = Either.Left(UpdateEnvironmentError.MapNotAllowed("parameters"))
        result.shouldBe(expected)
    }

    @Test
    fun `Does not allow lists`() = runTest {
        val parameters = Parameters.Map(
            "something" to Parameters.List(
                Parameters.String("Postman"),
                Parameters.String("Milkman"),
            ),
        )

        val result = updateEnvironment.run(parameters)

        val expected = Either.Left(UpdateEnvironmentError.ListNotAllowed("something"))
        result.shouldBe(expected)
    }
}