import logging

class JobRunner:
    def __init__(self, stepRunner):
        self._stepRunner = stepRunner
        self._logger = logging.getLogger(__name__)


    def runJob(self, job):
        self._logger.info(f"Running job: {job.name()}")

        environment = {}
        for step in job.steps():
            environment = self._stepRunner.runStep(step, environment)

        self._logger.info(f"Done jobbing")
