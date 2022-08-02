package engine

import arrow.core.Either
import configuration.installation.Job
import configuration.installation.Step
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.NextResult
import mocks.TestError
import mocks.engine.TestStepRunner
import plugins.ActionId
import kotlin.test.Test

@ExperimentalCoroutinesApi
class JobRunnerTest {
    private val stepRunner = TestStepRunner()
    private val jobRunner = JobRunnerImplementation(
        stepRunner,
    )
    private val job = Job(
        "Test job",
        listOf(
            Step(
                "Test step",
                ActionId("Some test")
            )
        )
    )

    @Test
    fun `Standard run`() = runTest {
        val result = jobRunner.run(job)

        result.shouldBe(Either.Right(emptyMap()))
    }

    @Test
    fun `Handle job with errors`() = runTest {
        val expectedFailure = TestError
        stepRunner.nextResult = NextResult.Failure(expectedFailure)

        val result = jobRunner.run(job)

        result.shouldBe(Either.Left(expectedFailure))
    }

    @Test
    fun `Environment is updated correctly`() = runTest {
        val initialEnvironment = mapOf(
            "do not touch" to "leave me alove",
            "change me" to "this is the old value",
            "another do not touch" to "I'm also not to be touched",
        )

        val environmentUpdates = mapOf(
            "first new value" to "hi, my name is (Slim Shady?)",
            "change me" to "new value here",
            "second new value" to "let's get started",
        )

        stepRunner.nextResult = NextResult.Success(environmentUpdates)
        val result = jobRunner.run(job, initialEnvironment)

        val expectedEnvironment = mapOf(
            "do not touch" to "leave me alove",
            "change me" to "new value here",
            "another do not touch" to "I'm also not to be touched",
            "first new value" to "hi, my name is (Slim Shady?)",
            "second new value" to "let's get started",
        )
        result.shouldBe(Either.Right(expectedEnvironment))
    }
}