package configuration.installation

import configuration.Parameters
import dk.rohdef.plugins.ActionId
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class StepSerializationTest {
    @Test
    fun `step with no parameters`() {
        val jsonNoMap = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3"
            }
        """.trimIndent()

        val jsonEmptyMap = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3",
                "parameters": {}
            }
        """.trimIndent()

        val noMapStep = Json.decodeFromString<Step>(jsonNoMap)
        val emptyMapStep = Json.decodeFromString<Step>(jsonEmptyMap)

        val expectedStep = Step(
            "my amazing step",
            ActionId("testPlugin@v3"),
            Parameters.Map(emptyMap())
        )

        noMapStep.shouldBe(expectedStep)
        emptyMapStep.shouldBe(expectedStep)
    }

    @Test
    fun `step with single string parameter`() {
        val json = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3",
                "parameters": {
                    "myKey": "some funny value"
                }
            }
        """.trimIndent()

        val step = Json.decodeFromString<Step>(json)

        val expectedStep = Step(
            "my amazing step",
            ActionId("testPlugin@v3"),
            Parameters.Map(
                mapOf(
                    "myKey" to Parameters.String("some funny value")
                )
            )
        )

        step.shouldBe(expectedStep)
    }

    @Test
    fun `step with single integer parameter`() {
        val json = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3",
                "parameters": {
                    "myKey": 23
                }
            }
        """.trimIndent()

        val step = Json.decodeFromString<Step>(json)

        val expectedStep = Step(
            "my amazing step",
            ActionId("testPlugin@v3"),
            Parameters.Map(
                mapOf(
                    "myKey" to Parameters.Integer(23)
                )
            )
        )

        step.shouldBe(expectedStep)
    }

    @Test
    fun `step with single list parameter`() {
        val json = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3",
                "parameters": {
                    "myKey": [
                        59,
                        "hello"
                    ]
                }
            }
        """.trimIndent()

        val step = Json.decodeFromString<Step>(json)

        val expectedStep = Step(
            "my amazing step",
            ActionId("testPlugin@v3"),
            Parameters.Map(
                mapOf(
                    "myKey" to Parameters.List(
                        listOf(
                            Parameters.Integer(59),
                            Parameters.String("hello")
                        )
                    )
                )
            )
        )

        step.shouldBe(expectedStep)
    }

    @Test
    fun `step with single map parameter`() {
        val json = """
            {
                "name": "my amazing step",
                "uses": "testPlugin@v3",
                "parameters": {
                    "myKey": {
                        "arg1": 11,
                        "arg2": "dancing"
                    }
                }
            }
        """.trimIndent()

        val step = Json.decodeFromString<Step>(json)

        val expectedStep = Step(
            "my amazing step",
            ActionId("testPlugin@v3"),
            Parameters.Map(
                mapOf(
                    "myKey" to Parameters.Map(
                        mapOf(
                            "arg1" to Parameters.Integer(11),
                            "arg2" to Parameters.String("dancing")
                        )
                    )
                )
            )
        )

        step.shouldBe(expectedStep)
    }
}