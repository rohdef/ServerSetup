import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.reflect.KSuspendFunction1

private val logger = KotlinLogging.logger {}

fun main(cliArguments: Array<String>) {
    val commandMapping = mapOf(
        Command.RUN to ::runRecipe,
        Command.GENERATE_SCRIPT to ::generateScript
    )

    actualMain(commandMapping, cliArguments)
}

typealias Executable = KSuspendFunction1<Array<String>, Unit>
fun actualMain(
    commands: Map<Command, Executable>,
    cliArguments: Array<String>
) = runBlocking {
    either {
        val restOfArguments = cliArguments
            .drop(1)
            .toTypedArray()

        val commandsWithDefault = commands
            .mapValues { it.value.right() }
            .withDefault { ExecutionError.MissingCommand(it).left() }

        val firstArgument = cliArguments
            .firstOrNull()
            .let { it?.right() ?: ExecutionError.NoArguments.left() }
            .map { it.toLowerCasePreservingASCIIRules() }
            .bind()

        val argumentNomalized = firstArgument
            .toUpperCasePreservingASCIIRules()
            .replace("-", "_")

        val command = enumValueOf<Command>(argumentNomalized)
            .bind()
        val subProgram = commandsWithDefault.getValue(command)
            .bind()
        subProgram(restOfArguments)
    }
}

/**
 * Returns an enum entry with the specified name or `null` if no such entry was found.
 */
inline fun <reified T : Enum<T>> enumValueOf(name: String): Either<ExecutionError.UnknownArgument, T> {
    return enumValues<T>().find { it.name == name }
        ?.right()
        ?: ExecutionError.UnknownArgument.left()
}

sealed interface ExecutionError {
    object NoArguments
    object UnknownArgument

    data class MissingCommand(val command: Command)
}

enum class Command {
    RUN,
    GENERATE_SCRIPT
}

suspend fun generateScript(cliArguments: Array<String>) {
    logger.info { "Running the generate script script" }
}