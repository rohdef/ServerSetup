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

    def provision(self):
        self._logger.info("Setting up basic server configuration")

        self._deleteUser(self._autoInstall.defaultIdentity())

        self._disableSnap()
        self._aptUpdate()
        self._installPackages(self._autoInstall.packages().packages())
        self._setupSsh(self._autoInstall.ssh())

        self._setShell(self._autoInstall.identity())

    def _disableSnap(self):
        self._logger.info("Disabling snap package manager")
        self._run(["apt-get", "-y", "purge snapd"], False)
        self._run(["rm", "-rf", "/snap", "/var/snap", "/var/lib/snapd"], False)

    def _aptUpdate(self):
        self._logger.info("Updating package lists")
        self._run(["apt-get", "update", False])

    def _installPackages(self, packages):
        self._logger.info(f"Installing packages: {packages}")
        installCommand = ["apt-get", "install", "-y"] + packages
        self._run(installCommand, False)

    def _setupSsh(self, ssh):
        self._logger.info(f"Setting up SSH server")
        if not ssh.allowPassword():
            self._logger.info(f"\tDisabling password login")

            self._run("sed -i \"/PasswordAuthentication/d\" /etc/ssh/sshd_config")
            self._run("sed -i \"/PermitRootLogin/d\" /etc/ssh/sshd_config")
            self._run("echo \"PasswordAuthentication no\" >> /etc/ssh/sshd_config")
            self._run("echo \"PermitRootLogin no\" >> /etc/ssh/sshd_config")

        self._run("service ssh reload")

    def _setShell(self, identity):
        username = identity.username()
        shell = identity.shell()

        self._logger.info(f"Updating shell for {username} to {shell}")
        self._run(f"chsh -s {shell} {username}")

    def _deleteUser(self, identity):
        username = identity.username()
        homeDirResult = self._runCapture(f"echo ~{username}")
        homeDir = homeDirResult.stdout.decode().strip()

        self._logger.info(f"Deleting user: {username}")
        self._run(f"userdel {username}")
        self._run(f"rm -rf {homeDir}")


if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logger.info("Provisioning server")
    server = PreparedServer(configuration)
    server.provision()

    logger.info("Done with Provisioning")
