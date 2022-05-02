#!/bin/env python3

from configuration.HostConfiguration import HostConfiguration
import logging
import os
import pwd
import sys

from System import System

class BlankServer(System):
    def __init__(self, configuration):
        self._configuration = configuration
        self._identity = configuration.autoInstall().defaultIdentity()

    def initServer(self, identity):
        self.changeHostName(identity.hostname())
        self.createNewUser(
            identity.username(),
            identity.realName(),
            identity.password()
        )
        self.copySshId(identity.username(), identity.authorizedKeys())

    def changeHostName(self, hostname):
        logging.info(f"Changing hostname to: {hostname}")
        self._run(f"hostnamectl set-hostname \"{hostname}\"")
        logging.info("\tHostname successfully updated")

    def createNewUser(self, username, realName, password):
        logging.info(f"Updating user details")
        
        logging.info(f"\tEnsuring user with username: \"{username}\"")
        try:
            pwd.getpwnam(username)
            logging.info(f"\tUser is already present")
        except KeyError:
            logging.info(f"\tCreating user with username: \"{username}\"")
            self._run(f"useradd -m -d /home/{username} -s /bin/bash {username}")
        
        logging.info(f"\tUpdating password for user: \"{username}\"")
        self._run(f"bash -c 'echo \"{username}:{password}\" | chpasswd'")

        logging.info(f"\tUpdating real name to \"{realName}\" for user \"{username}\"")
        self._run(f"usermod -c \"{realName}\" {username}")

        logging.info(f"\tUpdating to have sudo rights for user \"{username}\"")
        self._run(f"usermod -aG sudo {username}")

        logging.info(f"\tUser details updated successfully")

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

if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logging.basicConfig(level=configuration.logLevel())

    logging.info("Initializing server")    
    server = BlankServer(configuration)
    server.initServer(configuration.autoInstall().identity())

    logging.info("Done with intialization")
