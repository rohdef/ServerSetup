package utilities

import arrow.core.Either
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EnumsTest {
    @Test
    fun `Parse string to enum`() {
        val testA = Enums.enumValue<TestEnum>("VALUE_A")
        val testB = Enums.enumValue<TestEnum>("VALUE_B")

        testA.shouldBe(
            Either.Right(TestEnum.VALUE_A)
        )
        testB.shouldBe(
            Either.Right(TestEnum.VALUE_B)
        )
    }

    @Test
    fun `Description when value is not found`() {
        val test = Enums.enumValue<TestEnum>("NON_EXISTING_VALUE")

        test.shouldBe(
            Either.Left(
                EnumError.CannotParseToEnum(
                    "TestEnum",
                    "NON_EXISTING_VALUE",
                    listOf(TestEnum.VALUE_A, TestEnum.VALUE_B),
                )
            )
        )
    }

    enum class TestEnum {
        VALUE_A, VALUE_B
    }
}