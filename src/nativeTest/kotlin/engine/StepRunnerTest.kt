package engine

import configuration.Parameters
import configuration.engine.Runners
import configuration.engine.StepRunnerImplementation
import configuration.engine.VariableParser
import configuration.engine.VariableParserError
import configuration.installation.Step
import configuration.plugins.ActionId
import configuration.plugins.MissingPlugin
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import mocks.plugins.TestAction
import kotlin.test.Test

class StepRunnerTest {
    private val parser = VariableParser()

    @Test
    fun `Steps are distinguished by name`() {
        val fooId = ActionId("foo")
        val fooAction = TestAction()

        val barId = ActionId("bar")
        val barAction = TestAction()

        val runners = Runners(
            fooId to fooAction,
            barId to barAction,
        )

        val properties = emptyMap<String, String>()
        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            properties,
            runners,
            parser,
        )

        val step = Step(
            "dummy",
            fooId,
        )

        fooAction.executions.shouldHaveSize(0)
        barAction.executions.shouldHaveSize(0)

        val result = stepRunner.run(step, environment)

        result.shouldBeInstanceOf<Right<Any>>()
        fooAction.executions.shouldHaveSize(1)
        barAction.executions.shouldHaveSize(0)
    }

    @Test
    fun `Missing action for handling step`() {
        val fooId = ActionId("foo")
        val runners = Runners()

        val properties = emptyMap<String, String>()
        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            properties,
            runners,
            parser,
        )

        val step = Step(
            "dummy",
            fooId,
        )

        val result = stepRunner.run(step, environment)

        val expectedResult = Left(MissingPlugin(ActionId("foo")))
        result.shouldBe(expectedResult)
    }

    @Test
    fun `Environments are sent correctly`() {
        val fooId = ActionId("foo")
        val fooAction = TestAction()

        val runners = Runners(
            fooId to fooAction,
        )

        val properties = emptyMap<String, String>()
        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            properties,
            runners,
            parser,
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
            fooId,
            expectedParameters
        )

        stepRunner.run(step, environment)
        val execution = fooAction.executions.first()

        execution.parameters.shouldBe(expectedParameters)
    }

    @Test
    fun `Parameters are parsed`() {
        val fooId = ActionId("foo")
        val fooAction = TestAction()

        val runners = Runners(
            fooId to fooAction,
        )

        val properties = mapOf(
            "hostname" to "properties",
            "coffee" to "espresso",
        )
        val environment = mapOf(
            "hostname" to "environment",
            "wife" to "Camilla Lena F.P.",
        )
        val stepRunner = StepRunnerImplementation(
            properties,
            runners,
            parser,
        )

        val step = Step(
            "dummy",
            fooId,
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

        stepRunner.run(step, environment)
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
    fun `Parsing errors are passed on`() {
        val fooId = ActionId("foo")
        val fooAction = TestAction()

        val runners = Runners(
            fooId to fooAction,
        )

        val properties = emptyMap<String, String>()
        val environment = emptyMap<String, String>()
        val stepRunner = StepRunnerImplementation(
            properties,
            runners,
            parser,
        )

        val expectedParameters = Parameters.Map(
            "unknown reference" to Parameters.String("\$properties.something"),
        )
        val step = Step(
            "dummy",
            fooId,
            expectedParameters
        )

        val result = stepRunner.run(step, environment)
        result.shouldBe(Left(VariableParserError.VariableNotFound("\$properties.something")))
    }
}