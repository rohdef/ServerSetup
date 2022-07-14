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
        self._setDebConfSelections(self._autoInstall.debConfSelections().debConfSelections())
        self._installPackages(["slapd", "ldap-utils"])
        self._setLdapDefaults()


    def _setDebConfSelections(self, selections):
        echo = self._runPipe(["echo", "-e", selections])
        self._endPipe(["debconf-set-selections"], echo)


    def _installPackages(self, packages):
        self._logger.info(f"Installing packages: {packages}")
        installCommand = ["apt-get", "install", "-y"] + packages
        self._run(installCommand, False)

    def _setLdapDefaults(self):
        self._logger.info(f"Setting default settings for LDAP")
        self._run("sed -i \"/BASE/d\" /etc/ldap/ldap.conf")
        self._run("sed -i \"/URI/d\" /etc/ldap/ldap.conf")
        self._run("echo \"BASE	dc=cult-of-funny-hats,dc=dk\" >> /etc/ldap/ldap.conf")
        self._run("echo \"URI	ldap://ldap.cult-of-funny-hats.dk\" >> /etc/ldap/ldap.conf")


if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logger.info("Setting up server")
    server = ProvisionedServer(configuration)
    server.setup()

    logger.info("Done with setup")
