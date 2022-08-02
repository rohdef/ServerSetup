package configuration

import configuration.installation.Installation
import configuration.installation.JobId
import engine.Properties
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

data class Configuration(
    val installation: Installation,
    val properties: Properties,
    val jobsToRun: JobsToRun,
    val logLevel: LogLevel
) {
    constructor(arguments: Arguments): this(
        deserializeInstallation(arguments.configurationFile),
        arguments.properties,
        jobsToRun(arguments.jobs),
        arguments.logLevel,
    )

    companion object {
        private fun deserializeInstallation(deserializableInstallation: String): Installation =
            Json.decodeFromString<Installation>(deserializableInstallation)

        private fun jobsToRun(jobs: List<JobId>): JobsToRun {
            return if (jobs.size > 0) {
                JobsToRun.SelectedJobs(jobs)
            } else {
                JobsToRun.AllJobs
            }
        }
    }
}