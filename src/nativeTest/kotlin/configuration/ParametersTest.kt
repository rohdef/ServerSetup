package configuration

import io.kotest.matchers.shouldBe
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import kotlin.test.Test

class ParametersTest {
    class Map {
        private val inputMap = Parameters.Map(
            "age" to Parameters.Integer(4),
            "age as string" to Parameters.String("4"),

            "truthful" to Parameters.String("no way in this life"),
            "truthful as boolean" to Parameters.Boolean(true),

            "enabled" to Parameters.Boolean(true),
            "enabled items" to Parameters.List(),

            "shopping" to Parameters.List(
                Parameters.String("Plantgurt"),
                Parameters.Integer(3),
            ),
            "shopping grouped" to Parameters.Map(
                "breakfast" to Parameters.String("Plantgurt"),
                "amount" to Parameters.Integer(3),
            ),

            "permissions" to Parameters.Map(
                "read" to Parameters.Boolean(true),
                "write" to Parameters.Boolean(false),
                "execute" to Parameters.Boolean(true),
            ),
            "permissions numbered" to Parameters.Integer(5),
        )

        @Test
        fun `integer`() {
            val integer = inputMap.integerValue("age")

            integer.shouldBe(Right(4))
        }

        @Test
        fun `missing integer`() {
            val integer = inputMap.integerValue("old age")

            integer.shouldBe(
                Left(
                    ParameterError.UnknownKey("old age", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not integer`() {
            val integer = inputMap.integerValue("age as string")

            integer.shouldBe(Left(ParameterError.WrongType))
        }

        @Test
        fun `string`() {
            val string = inputMap.stringValue("truthful")

            string.shouldBe(Right("b"))
        }

        @Test
        fun `missing string`() {
            val string = inputMap.stringValue("truthful liar")

            string.shouldBe(
                Left(
                    ParameterError.UnknownKey("truthful liar", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not string`() {
            val string = inputMap.stringValue("truthful as boolean")

            string.shouldBe(Left(ParameterError.WrongType))
        }

        @Test
        fun `boolean`() {
            val boolean = inputMap.booleanValue("enabled")

            boolean.shouldBe(Right(true))
        }

        @Test
        fun `missing boolean`() {
            val boolean = inputMap.booleanValue("enabled after dark")

            boolean.shouldBe(
                Left(
                    ParameterError.UnknownKey("enabled after dark", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not boolean`() {
            val boolean = inputMap.booleanValue("enabled items")

            boolean.shouldBe(Left(ParameterError.WrongType))
        }

        @Test
        fun `list`() {
            val list = inputMap.listValue("shopping")

            list.shouldBe(
                Right(
                    listOf(
                        Parameters.String("Plantgurt"),
                        Parameters.Integer(3),
                    )
                )
            )
        }

        @Test
        fun `missing list`() {
            val list = inputMap.listValue("shopping grouped")

            list.shouldBe(
                Left(
                    ParameterError.UnknownKey("shopping fashion", inputMap.value.keys)
                )
            )

        }

        @Test
        fun `type is not list`() {
            val list = inputMap.listValue("shopping fashion")

            list.shouldBe(Left(ParameterError.WrongType))
        }

        @Test
        fun `map`() {
            val map = inputMap.mapValue("permissions")

            map.shouldBe(
                Right(
                    mapOf(
                        "read" to Parameters.Boolean(true),
                        "write" to Parameters.Boolean(false),
                        "execute" to Parameters.Boolean(true),
                    )
                )
            )
        }

        @Test
        fun `missing map`() {
            val map = inputMap.mapValue("permissions numbered")

            map.shouldBe(
                Left(
                    ParameterError.UnknownKey("permissions windows", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not map`() {
            val map = inputMap.mapValue("permissions windows")

            map.shouldBe(Left(ParameterError.WrongType))
        }
    }
}