package configuration

import configuration.installation.JobId

sealed class JobsToRun {
    abstract fun accept(jobId: JobId): Boolean

    object AllJobs : JobsToRun() {
        override fun accept(jobId: JobId): Boolean = true
    }

    data class SelectedJobs(val jobs: List<JobId>) : JobsToRun() {
        override fun accept(jobId: JobId): Boolean = jobs.contains(jobId)
    }
}
