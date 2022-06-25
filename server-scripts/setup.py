#!/bin/env python3

from configuration.HostConfiguration import HostConfiguration
import logging
import logging.config
import os
import sys

from System import System

class ProvisionedServer(System):
    def __init__(self, configuration):
        self._configuration = configuration
        self._autoInstall = self._configuration.autoInstall()

        self._logger = logging.getLogger(__name__)

    def setup(self):
        self._logger.info("Setting up server")


if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logger.info("Setting up server")
    server = ProvisionedServer(configuration)
    server.setup()

    logger.info("Done with setup")
