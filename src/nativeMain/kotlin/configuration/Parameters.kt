package configuration

import arrow.core.Either
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

@kotlinx.serialization.Serializable(with = ParametersSerializer::class)
sealed interface Parameters {
    @kotlinx.serialization.Serializable(with = IntegerParameterSerializer::class)
    data class Integer(val value: Int) : Parameters

    @kotlinx.serialization.Serializable(with = ListParameterSerializer::class)
    data class List(val value: kotlin.collections.List<Parameters>) : Parameters {
        constructor(vararg parameters: Parameters) : this(listOf(*parameters))
    }

    @kotlinx.serialization.Serializable(with = MapParameterSerializer::class)
    data class Map(val value: kotlin.collections.Map<kotlin.String, Parameters>) : Parameters {
        fun integerValue(key: kotlin.String): Either<ParameterError, Int> {
            return getValue(key, Integer::class)
                .map { it.value }
        }

        fun integerValue(key: kotlin.String, default: Int): Either<ParameterError, Int> {
            return getValue(key, Integer(default), Integer::class)
                .map { it.value }
        }

        fun stringValue(key: kotlin.String): Either<ParameterError, kotlin.String> {
            return getValue(key, String::class)
                .map { it.value }
        }

        fun stringValue(key: kotlin.String, default: kotlin.String): Either<ParameterError, kotlin.String> {
            return getValue(key, String(default), String::class)
                .map { it.value }
        }

        fun listValue(key: kotlin.String): Either<ParameterError, kotlin.collections.List<Parameters>> {
            return getValue(key, List(), List::class)
                .map { it.value }
        }

        fun mapValue(key: kotlin.String): Either<ParameterError, kotlin.collections.Map<kotlin.String, Parameters>> {
            return getValue(key, Map(), Map::class)
                .map { it.value }
        }

        private fun <T : Parameters> getValue(key: kotlin.String, kClass: KClass<T>): Either<ParameterError, T> {
            val parameter = value[key]

            return when {
                kClass.isInstance(parameter) -> Either.Right(kClass.cast(parameter))
                parameter == null -> Either.Left(ParameterError.UnknownKey(key, value.keys))
                else -> Either.Left(ParameterError.WrongType(key, kClass, parameter::class))
            }
        }

        private fun <T : Parameters> getValue(key: kotlin.String, default: T, kClass: KClass<T>): Either<ParameterError, T> {
            val parameter = value[key]

            return when {
                kClass.isInstance(parameter) -> Either.Right(parameter as T)
                parameter == null -> Either.Right(default)
                else -> Either.Left(ParameterError.WrongType(key, kClass, parameter::class))
            }
        }

        constructor(vararg pairs: Pair<kotlin.String, Parameters>) : this(mapOf(*pairs))
    }

    @kotlinx.serialization.Serializable(with = StringParameterSerializer::class)
    data class String(val value: kotlin.String) : Parameters
}

sealed interface ParameterError {
    data class UnknownKey(
        val missingKey: String,
        val validKeys: Set<String>,
    ) : ParameterError

    data class WrongType(
        val key: String,
        val expectedType: KClass<out Parameters>,
        val actualType: KClass<out Parameters>,
    ) : ParameterError
}


abstract class BaseParametersSerializer<T : Parameters> : KSerializer<T> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: T) {
        val jsonElement = toJsonElement(value)

        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    private fun toJsonElement(value: Parameters): JsonElement = when (value) {
        is Parameters.Map -> JsonObject(
            value.value.mapValues { toJsonElement(it.value) }
        )

        is Parameters.List -> JsonArray(
            value.value.map { toJsonElement(it) }
        )

        is Parameters.Integer -> JsonPrimitive(value.value)
        is Parameters.String -> JsonPrimitive(value.value)
    }

    override fun deserialize(decoder: Decoder): T {
        with(decoder as JsonDecoder) {
            val jsonElement = decodeJsonElement()

            return deserializeJson(jsonElement)
        }
    }

    abstract fun deserializeJson(jsonElement: JsonElement): T
}

object ParametersSerializer : BaseParametersSerializer<Parameters>() {
    override fun deserializeJson(jsonElement: JsonElement): Parameters {
        return when (jsonElement) {
            is JsonPrimitive -> fixPrimitive(jsonElement)
            is JsonObject -> MapParameterSerializer.deserializeJson(jsonElement)
            is JsonArray -> ListParameterSerializer.deserializeJson(jsonElement)
            else -> throw IllegalArgumentException("Only integers, strings, maps and list are allowed here")
        }
    }

    private fun fixPrimitive(primitive: JsonPrimitive): Parameters {
        return when {
            primitive.isString -> StringParameterSerializer.deserializeJson(primitive)
            else -> IntegerParameterSerializer.deserializeJson(primitive)
        }
    }
}

object StringParameterSerializer : BaseParametersSerializer<Parameters.String>() {
    override fun deserializeJson(jsonElement: JsonElement): Parameters.String {
        return when (jsonElement) {
            is JsonPrimitive -> when {
                jsonElement.isString -> Parameters.String(jsonElement.content)
                else -> throw IllegalArgumentException("Only strings are allowed here")
            }

            else -> throw IllegalArgumentException("Only strings are allowed here")
        }
    }
}

object IntegerParameterSerializer : BaseParametersSerializer<Parameters.Integer>() {
    override fun deserializeJson(jsonElement: JsonElement): Parameters.Integer {
        return when (jsonElement) {
            is JsonPrimitive -> Parameters.Integer(jsonElement.int)
            else -> throw IllegalArgumentException("Only ints are allowed here")
        }
    }
}

object MapParameterSerializer : BaseParametersSerializer<Parameters.Map>() {
    override fun deserializeJson(jsonElement: JsonElement): Parameters.Map {
        return when (jsonElement) {
            is JsonObject -> Parameters.Map(jsonElement.mapValues { ParametersSerializer.deserializeJson(it.value) })
            else -> throw IllegalArgumentException("Only maps are allowed here")
        }
    }
}

object ListParameterSerializer : BaseParametersSerializer<Parameters.List>() {
    override fun deserializeJson(jsonElement: JsonElement): Parameters.List {
        return when (jsonElement) {
            is JsonArray -> Parameters.List(jsonElement.map { ParametersSerializer.deserializeJson(it) })
            else -> throw IllegalArgumentException("Only maps are allowed here")
        }
    }
}