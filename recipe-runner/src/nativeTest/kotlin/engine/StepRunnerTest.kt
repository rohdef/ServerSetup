package engine

import arrow.core.Either
import configuration.Configuration
import configuration.JobsToRun
import configuration.LogLevel
import configuration.Parameters
import configuration.installation.Installation
import configuration.installation.Step
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mocks.plugins.TestAction
import plugins.ActionId
import plugins.MissingPlugin
import kotlin.test.Test

@ExperimentalCoroutinesApi
class StepRunnerTest {
    private val baseConfiguration = Configuration(
        Installation(emptyMap()),
        emptyMap(),
        JobsToRun.AllJobs,
        LogLevel.INFO,
    )

    private val parser = VariableParser()

    @Test
    fun `Steps are distinguished by name`() = runTest {
        val fooAction = TestAction()
        val barAction = TestAction()

        val runners = Runners(
            fooAction,
            barAction,
        )

        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            runners,
            parser,
            baseConfiguration,
        )

        val step = Step(
            "dummy",
            fooAction.actionId,
        )

        fooAction.executions.shouldHaveSize(0)
        barAction.executions.shouldHaveSize(0)

        val result = stepRunner.runStep(step, environment)

        result.shouldBeInstanceOf<Either.Right<Any>>()
        fooAction.executions.shouldHaveSize(1)
        barAction.executions.shouldHaveSize(0)
    }

    @Test
    fun `Missing action for handling step`() = runTest {
        val fooId = ActionId("foo")
        val runners = Runners()

        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            runners,
            parser,
            baseConfiguration,
        )

        val step = Step(
            "dummy",
            fooId,
        )

        val result = stepRunner.runStep(step, environment)

        val expectedResult = Either.Left(MissingPlugin(ActionId("foo")))
        result.shouldBe(expectedResult)
    }

    @Test
    fun `Environments are sent correctly`() = runTest {
        val fooAction = TestAction()

        val runners = Runners(fooAction)

        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            runners,
            parser,
            baseConfiguration,
        )

        val expectedParameters = Parameters.Map(
            "phone" to Parameters.String("FairPhone"),
            "year" to Parameters.Integer(1992),
            "dates" to Parameters.Map(
                "important" to Parameters.String("2022-05-07"),
                "karl" to Parameters.String("1987-01-13"),
                "anne" to Parameters.String("1989-09-18"),
            ),
        )
        val step = Step(
            "dummy",
            fooAction.actionId,
            expectedParameters
        )

        stepRunner.runStep(step, environment)
        val execution = fooAction.executions.first()

        execution.parameters.shouldBe(expectedParameters)
    }

    @Test
    fun `Parameters are parsed`() = runTest {
        val fooAction = TestAction()

        val runners = Runners(fooAction)

        val properties = mapOf(
            "hostname" to "properties",
            "coffee" to "espresso",
        )
        val environment = mapOf(
            "hostname" to "environment",
            "wife" to "Camilla Lena F.P.",
        )
        val stepRunner = StepRunnerImplementation(
            runners,
            parser,
            baseConfiguration.copy(properties = properties),
        )

        val step = Step(
            "dummy",
            fooAction.actionId,
            Parameters.Map(
                "properties" to Parameters.Map(
                    "hostname" to Parameters.String("\$properties.hostname"),
                    "coffee" to Parameters.String("\$properties.coffee"),
                ),
                "environment" to Parameters.Map(
                    "hostname" to Parameters.String("\$environment.hostname"),
                    "wife" to Parameters.String("\$environment.wife"),
                )
            )
        )

        stepRunner.runStep(step, environment)
        val execution = fooAction.executions.first()

        val expectedParameters = Parameters.Map(
            "properties" to Parameters.Map(
                "hostname" to Parameters.String("properties"),
                "coffee" to Parameters.String("espresso"),
            ),
            "environment" to Parameters.Map(
                "hostname" to Parameters.String("environment"),
                "wife" to Parameters.String("Camilla Lena F.P."),
            )
        )

        execution.parameters.shouldBe(expectedParameters)
    }

    @Test
    fun `Parsing errors are passed on`() = runTest {
        val fooAction = TestAction()

        val runners = Runners(fooAction)

        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            runners,
            parser,
            baseConfiguration,
        )

        val expectedParameters = Parameters.Map(
            "unknown reference" to Parameters.String("\$properties.something"),
        )
        val step = Step(
            "dummy",
            fooAction.actionId,
            expectedParameters
        )

        val result = stepRunner.runStep(step, environment)
        result.shouldBe(Either.Left(VariableParserError.VariableNotFound("\$properties.something")))
    }
}