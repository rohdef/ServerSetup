#!/bin/env python3

from configuration.Configuration import Configuration
from configuration.Configuration import JobRunRule
from JobRunner import JobRunner
from StepRunner import StepRunner

import logging
import logging.config
import os
import sys
import yaml

if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    logger.info("Setting up server")
    configuration = Configuration(sys.argv[1:], currentPath)

    stepRunner = StepRunner()
    jobRunner = JobRunner(stepRunner)
    installation = configuration.installation()

    for jobName, job in installation.jobs().items():
        if configuration.jobRunRule() == JobRunRule.ALL:
            jobRunner.runJob(job)
        elif configuration.jobRunRule() == JobRunRule.SELECTED and jobName in configuration.jobsToRun():
            jobRunner.runJob(job)

    logger.info("Done with recipe")
