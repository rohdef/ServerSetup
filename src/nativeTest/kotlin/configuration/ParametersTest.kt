package configuration

import arrow.core.Either
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ParametersTest {
    class Map {
        private val inputMap = Parameters.Map(
            "age" to Parameters.Integer(4),
            "age as string" to Parameters.String("4"),

            "truthful" to Parameters.String("no way in this life"),
            "truthful as boolean" to Parameters.Integer(0),

            "shopping" to Parameters.List(
                Parameters.String("Plantgurt"),
                Parameters.Integer(3),
            ),
            "shopping grouped" to Parameters.Map(
                "breakfast" to Parameters.String("Plantgurt"),
                "amount" to Parameters.Integer(3),
            ),

            "permissions" to Parameters.Map(
                "read" to Parameters.String("true"),
                "write" to Parameters.String("false"),
                "execute" to Parameters.String("true"),
            ),
            "permissions numbered" to Parameters.Integer(5),
        )

        @Test
        fun `integer value`() {
            val integer = inputMap.integerValue("age")

            integer.shouldBe(Either.Right(4))
        }

        @Test
        fun `missing integer`() {
            val integer = inputMap.integerValue("old age")

            integer.shouldBe(
                Either.Left(
                    ParameterError.UnknownKey("old age", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not integer`() {
            val integer = inputMap.integerValue("age as string")

            integer.shouldBe(
                Either.Left(
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

            integer.shouldBe(Either.Right(4))
        }

        @Test
        fun `missing integer with default`() {
            val integer = inputMap.integerValue("old age", 101)

            integer.shouldBe(Either.Right(101))
        }

        @Test
        fun `type is not integer with default`() {
            val integer = inputMap.integerValue("age as string")

            integer.shouldBe(
                Either.Left(
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

            string.shouldBe(Either.Right("no way in this life"))
        }

        @Test
        fun `missing string`() {
            val string = inputMap.stringValue("truthful liar")

            string.shouldBe(
                Either.Left(
                    ParameterError.UnknownKey("truthful liar", inputMap.value.keys)
                )
            )
        }

        @Test
        fun `type is not string`() {
            val string = inputMap.stringValue("truthful as boolean")

            string.shouldBe(
                Either.Left(
                    ParameterError.WrongType(
                        "truthful as boolean",
                        Parameters.String::class,
                        Parameters.Integer::class,
                    )
                )
            )
        }

        @Test
        fun `string value with default`() {
            val string = inputMap.stringValue("truthful", "always tell the truth")

            string.shouldBe(Either.Right("no way in this life"))
        }

        @Test
        fun `missing string with default`() {
            val string = inputMap.stringValue("truthful liar", "always tell the truth")

            string.shouldBe(Either.Right("always tell the truth"))
        }

        @Test
        fun `type is not string with default`() {
            val string = inputMap.stringValue("truthful as boolean", "always tell the truth")

            string.shouldBe(
                Either.Left(
                    ParameterError.WrongType(
                        "truthful as boolean",
                        Parameters.String::class,
                        Parameters.Integer::class,
                    )
                )
            )
        }

        @Test
        fun `list values`() {
            val list = inputMap.listValue("shopping")

            list.shouldBe(
                Either.Right(
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

            list.shouldBe(Either.Right(emptyList()))
        }

        @Test
        fun `type is not list`() {
            val list = inputMap.listValue("shopping grouped")

            list.shouldBe(
                Either.Left(
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
                Either.Right(
                    mapOf(
                        // TODO enum?
                        "read" to Parameters.String("true"),
                        "write" to Parameters.String("false"),
                        "execute" to Parameters.String("true"),
                    )
                )
            )
        }

        @Test
        fun `missing map`() {
            val map = inputMap.mapValue("permissions windows")

            map.shouldBe(Either.Right(emptyMap()))
        }

        @Test
        fun `type is not map`() {
            val map = inputMap.mapValue("permissions numbered")

            map.shouldBe(
                Either.Left(
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