package utilities

import arrow.core.Either

class Enums {
    companion object {
        inline fun <reified T : Enum<T>> enumValue(key: String): Either<EnumError, T> {
            try {
                val enumValueOf: T = enumValueOf(key)
                return Either.Right(enumValueOf)
            } catch (e: Exception) {

                return Either.Left(
                    EnumError.CannotParseToEnum<T>(
                        T::class.simpleName!!,
                        key,
                        listOf(*enumValues()),
                    )
                )
            }
        }

    }
}

sealed interface EnumError {
    data class CannotParseToEnum<T>(
        val enum: String,
        val attemptedValue: String,
        val validValues: List<T>,
    ) : EnumError
}