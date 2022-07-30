package configuration

import engine.Properties
import configuration.installation.JobId
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple

data class Arguments(
    val jobs: List<JobId>,
    val logLevel: LogLevel,
    val properties: Properties,
    val configurationFile: String,
) {
    companion object {
        fun parseArguments(arguments: Array<String>): Arguments {
            val parser = ArgParser("./gourmet")

            val jobs by parser
                .option(
                    ArgType.String,
                    "job",
                    "j",
                    "job to run (specify multiple times for multiple jobs)"
                )
                .multiple()

            val logLevel by parser
                .option(
                    ArgType.Choice<LogLevel>(),
                    "logLevel",
                    "l",
                    "Log level",
                )
                .default(LogLevel.INFO)

            val properties by parser
                .option(
                    ArgType.String,
                    "property",
                    "p",
                    "Property for the recipe (specify multiple times for multiple properties)",
                )
                .multiple()

            val configurationFile = parser
                .argument(
                    ArgType.String,
                    "recipe",
                    "The recipe to run"
                )


            parser.parse(arguments)

            val jobsAsIds = jobs
                .map { JobId(it) }
            val propertiesAsMap = properties
                .map { it.split("=") }
                .associate { it[0] to it[1] }
            val configurationFileValue = configurationFile.value

            return Arguments(
                jobsAsIds,
                logLevel,
                propertiesAsMap,
                configurationFileValue,
            )
        }
    }
}