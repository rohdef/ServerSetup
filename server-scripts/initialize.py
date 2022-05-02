#!/bin/env python3

from configuration.HostConfiguration import HostConfiguration
import logging
import logging.config
import os
import pwd
import sys

from System import System

class BlankServer(System):
    def __init__(self, configuration):
        self._configuration = configuration
        self._identity = configuration.autoInstall().defaultIdentity()

        self._logger = logging.getLogger(__name__)

    def initServer(self, identity):
        self.changeHostName(identity.hostname())
        self.createNewUser(
            identity.username(),
            identity.realName(),
            identity.password()
        )
        self.copySshId(identity.username(), identity.authorizedKeys())
        self.copyInitScripts(identity.username())

    def changeHostName(self, hostname):
        self._logger.info(f"Changing hostname to: {hostname}")
        self._run(f"hostnamectl set-hostname \"{hostname}\"")
        self._logger.info("\tHostname successfully updated")

    def createNewUser(self, username, realName, password):
        self._logger.info(f"Updating user details")
        
        self._logger.info(f"\tEnsuring user with username: \"{username}\"")
        try:
            pwd.getpwnam(username)
            self._logger.info(f"\tUser is already present")
        except KeyError:
            self._logger.info(f"\tCreating user with username: \"{username}\"")
            self._run(f"useradd -m -d /home/{username} -s /bin/bash {username}")
        
        self._logger.info(f"\tUpdating password for user: \"{username}\"")
        self._run(f"bash -c 'echo \"{username}:{password}\" | chpasswd'")

        self._logger.info(f"\tUpdating real name to \"{realName}\" for user \"{username}\"")
        self._run(f"usermod -c \"{realName}\" {username}")

        self._logger.info(f"\tUpdating to have sudo rights for user \"{username}\"")
        self._run(f"usermod -aG sudo {username}")

        self._logger.info(f"\tUser details updated successfully")

    def copySshId(self, username, rsaKeys):
        homeDirResult = self._runCapture(f"echo ~{username}")
        homeDir = homeDirResult.stdout.decode().strip()

        sshDir = os.path.join(homeDir, ".ssh")
        self._runAsUser(username, f"mkdir -p {sshDir}")
        self._run(f"chmod 700 {sshDir}")
        
        authorizedKeysPath = os.path.join(sshDir, "authorized_keys")
        self._runAsUser(username, f"touch {authorizedKeysPath}")
        self._run(f"chmod 600 {authorizedKeysPath}")
        with open(authorizedKeysPath, "w") as keys:
            keys.write(rsaKeys)

    def copyInitScripts(self, username):
        homeDirResult = self._runCapture(f"echo ~{username}")
        homeDir = homeDirResult.stdout.decode().strip()

        currentPath = self._configuration.currentPath()
        self._run(f"rm -rf \"{homeDir}/do-configure\"")
        self._run(f"cp -r \"{currentPath}\" \"{homeDir}\"")
        self._run(f"chown -R {username}:{username} \"{homeDir}/do-configure\"")

if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    logging.config.fileConfig(f"{currentPath}/logging.conf")
    logger = logging.getLogger(__name__)

    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logger.info("Initializing server")
    server = BlankServer(configuration)
    server.initServer(configuration.autoInstall().identity())

    logger.info("Done with intialization")
