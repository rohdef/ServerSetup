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
        fun `integer value`() {
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

            integer.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "age as string",
                        Parameters.Integer::class,
                        Parameters.String::class,
                    )
                )
            )
        }

        @Test
        fun `integer value with default`() {
            val integer = inputMap.integerValue("age", 101)

            integer.shouldBe(Right(4))
        }

        @Test
        fun `missing integer with default`() {
            val integer = inputMap.integerValue("old age", 101)

            integer.shouldBe(Right(101))
        }

        @Test
        fun `type is not integer with default`() {
            val integer = inputMap.integerValue("age as string")

            integer.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "age as string",
                        Parameters.Integer::class,
                        Parameters.String::class,
                    )
                )
            )
        }

        @Test
        fun `string value`() {
            val string = inputMap.stringValue("truthful")

            string.shouldBe(Right("no way in this life"))
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

            string.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "truthful as boolean",
                        Parameters.String::class,
                        Parameters.Boolean::class,
                    )
                )
            )
        }

        @Test
        fun `string value with default`() {
            val string = inputMap.stringValue("truthful", "always tell the truth")

            string.shouldBe(Right("no way in this life"))
        }

        @Test
        fun `missing string with default`() {
            val string = inputMap.stringValue("truthful liar", "always tell the truth")

            string.shouldBe(Right("always tell the truth"))
        }

        @Test
        fun `type is not string with default`() {
            val string = inputMap.stringValue("truthful as boolean", "always tell the truth")

            string.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "truthful as boolean",
                        Parameters.String::class,
                        Parameters.Boolean::class,
                    )
                )
            )
        }

        @Test
        fun `boolean value`() {
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

            boolean.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "enabled items",
                        Parameters.Boolean::class,
                        Parameters.List::class,
                    )
                )
            )
        }

        @Test
        fun `boolean value with default`() {
            val boolean = inputMap.booleanValue("enabled", false)

            boolean.shouldBe(Right(true))
        }

        @Test
        fun `missing boolean with default`() {
            val boolean = inputMap.booleanValue("enabled after dark", true)

            boolean.shouldBe(Right(true))
        }

        @Test
        fun `type is not boolean with default`() {
            val boolean = inputMap.booleanValue("enabled items", false)

            boolean.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "enabled items",
                        Parameters.Boolean::class,
                        Parameters.List::class,
                    )
                )
            )
        }

        @Test
        fun `list values`() {
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
            val list = inputMap.listValue("shopping fashion")

            list.shouldBe(Right(emptyList()))
        }

        @Test
        fun `type is not list`() {
            val list = inputMap.listValue("shopping grouped")

            list.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "shopping grouped",
                        Parameters.List::class,
                        Parameters.Map::class,
                    )
                )
            )

        }

        @Test
        fun `map values`() {
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
            val map = inputMap.mapValue("permissions windows")

            map.shouldBe(Right(emptyMap()))
        }

        @Test
        fun `type is not map`() {
            val map = inputMap.mapValue("permissions numbered")

            map.shouldBe(
                Left(
                    ParameterError.WrongType(
                        "permissions numbered",
                        Parameters.Map::class,
                        Parameters.Integer::class,
                    )
                )
            )
        }
    }
}