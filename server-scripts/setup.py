#!/bin/env python3

from configuration.HostConfiguration import HostConfiguration
import logging
import logging.config
import os
import sys

from System import System

class PreparedServer(System):
    def __init__(self, configuration):
        self._configuration = configuration
        self._autoInstall = self._configuration.autoInstall()

        self._logger = logging.getLogger(__name__)

    def setup(self):
        self._logger.info("Pretending to do setup")

        self._disableSnap()
        self._installBasePackages([])

    def _disableSnap(self):
        pass

    def _installBasePackages(self, packages):
        self._logger.info("Updating package lists")
        self._run("apt-get update")

        self._logger.info("Installing base packages")
        packages = self._autoInstall.packages().packages()
        installCommand = ["apt-get", "install", "-y"] +  packages
        self._run(installCommand, False)


if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logger.info("Setting up server")
    server = PreparedServer(configuration)
    server.setup()

    logger.info("Done with setup")
