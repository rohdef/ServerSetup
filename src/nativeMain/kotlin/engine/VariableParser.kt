package engine

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.traverseEither
import configuration.Parameters

class VariableParser {
    private fun parse(
        properties: Properties,
        environment: Environment,
        parameters: Parameters
    ) : Either<VariableParserError, Parameters> {
        return when(parameters) {
            is Parameters.Integer -> Either.Right(parameters)
            is Parameters.List -> parse(properties, environment, parameters)
            is Parameters.Map -> parse(properties, environment, parameters)
            is Parameters.String -> parse(properties, environment, parameters)
        }
    }

    fun parse(
        properties: Properties,
        environment: Environment,
        parameters: Parameters.Map
    ): Either<VariableParserError, Parameters.Map> {
        val items = parameters.value.mapValues { parse(properties, environment, it.value) }

        val newItems = mutableMapOf<String, Parameters>()
        for (item in items) {
            val value = item.value
            when (value) {
                is Either.Left -> return value
                is Either.Right -> newItems[item.key] = value.value
            }
        }
        return Either.Right(Parameters.Map(newItems.toMap()))
    }

    private fun parse(
        properties: Properties,
        environment: Environment,
        parameters: Parameters.List
    ) : Either<VariableParserError, Parameters.List> {
        val items = parameters.value.map { parse(properties, environment, it) }

        val newItems = mutableListOf<Parameters>()
        for (item in items) {
            when (item) {
                is Either.Left -> return item
                is Either.Right -> newItems.add(item.value)
            }
        }

        return Either.Right(Parameters.List(newItems.toList()))
    }

    private fun parse(
        properties: Properties,
        environment: Environment,
        parameters: Parameters.String
    ) : Either<VariableParserError, Parameters.String> {
        return parse(properties, environment, parameters.value)
            .map { Parameters.String(it) }
    }

    fun parse(
        properties: Properties,
        environment: Environment,
        value: String
    ): Either<VariableParserError, String> {
        val valueWithProperties = parsePlaceholder(properties, "\$properties", value)
        val valueWithPropertiesAndEnvironment = valueWithProperties.flatMap {
            parsePlaceholder(environment, "\$environment", it)
        }

        return valueWithPropertiesAndEnvironment
    }

    private fun parsePlaceholder(
        variables: Properties,
        placeholder: String,
        parameter: String,
    ): Either<VariableParserError, String> {
        val searchPattern = "\\$placeholder\\.(\\w+)".toRegex()
        val referencedVariables = searchPattern
            .findAll(parameter)
            .map { it.value }
            .distinct()

        val variablesEithered = variables
            .mapKeys { "$placeholder.${it.key}" }
            .mapValues { Either.Right(it.value) }
            .withDefault { Either.Left(VariableParserError.VariableNotFound(it)) }

        val variableToValue = referencedVariables.traverseEither { key ->
            variablesEithered.getValue(key)
                .map { key to it }
        }

        return variableToValue.map {
            it.fold(parameter) { acc, pair ->
                acc.replace(pair.first, pair.second)
            }
        }
    }
}

sealed interface VariableParserError : EngineError {
    data class VariableNotFound(val variables: String) : VariableParserError
}