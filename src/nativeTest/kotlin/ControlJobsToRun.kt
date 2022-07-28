import configuration.installation.JobId
import configuration.JobsToRun
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ControlJobsToRun {
    @Test
    fun `AllJobs accepts everything`() {
        val jobsToRun = JobsToRun.AllJobs

        jobsToRun.accept(JobId("42")).shouldBe(true)
        jobsToRun.accept(JobId("test")).shouldBe(true)
        jobsToRun.accept(JobId("complex name")).shouldBe(true)
    }

    @Test
    fun `Empty SelectedJobs accepts nothing`() {
        val jobsToRun = JobsToRun.SelectedJobs(emptyList())

        jobsToRun.accept(JobId("42")).shouldBe(false)
        jobsToRun.accept(JobId("test")).shouldBe(false)
        jobsToRun.accept(JobId("complex name")).shouldBe(false)
    }

    @Test
    fun `All jobs in SelectedJobs accepts everything`() {
        val jobsToRun = JobsToRun.SelectedJobs(
            listOf(
                JobId("42"),
                JobId("test"),
                JobId("complex name"),
            )
        )

        jobsToRun.accept(JobId("42")).shouldBe(true)
        jobsToRun.accept(JobId("test")).shouldBe(true)
        jobsToRun.accept(JobId("complex name")).shouldBe(true)
    }

    @Test
    fun `"test" in SelectedJobs accepts "test"`() {
        val jobsToRun = JobsToRun.SelectedJobs(
            listOf(
                JobId("test"),
            )
        )

        jobsToRun.accept(JobId("42")).shouldBe(false)
        jobsToRun.accept(JobId("test")).shouldBe(true)
        jobsToRun.accept(JobId("complex name")).shouldBe(false)
    }

    @Test
    fun `"42" and "complex name" in SelectedJobs accepts "42" and "complex name"`() {
        val jobsToRun = JobsToRun.SelectedJobs(
            listOf(
                JobId("42"),
                JobId("complex name"),
            )
        )

        jobsToRun.accept(JobId("42")).shouldBe(true)
        jobsToRun.accept(JobId("test")).shouldBe(false)
        jobsToRun.accept(JobId("complex name")).shouldBe(true)
    }
}