import logging
import os
import shutil
import time
from subprocess import run

from PreparedServer import PreparedServer

logger = logging.getLogger(__name__)

class BlankServer:
    def __init__(self, configuration):
        self._configuration = configuration

        identity = configuration.autoInstall().defaultIdentity()
        self._host = configuration.clientHost()
        self._username = identity.username()
        self._password = identity.password()

    def initServer(self):
        logger.info(f"Initializing server: {self._host}")

        logger.info("Preparing files for server")
        self._prepareFiles("do-configure")

        logger.info("Copying data to server")
        self._runSshCommand(f"export SUDO_ASKPASS=/home/{self._username}/do-configure/ask-pass-default.py; sudo --askpass rm -r /home/ubuntu/do-configure || true")
        self._copyFiles("do-configure")

        logger.info("Executing initialize scripts")
        self._runSshCommand("~/do-configure/initialize.sh")

        self._reboot(self._username)

        logger.info("Done initializing ready to perform actions on the server")
        return PreparedServer(self._configuration)

    def _prepareFiles(self, destination):
        destinationPath = os.path.join(".", destination)
        if os.path.isdir(destinationPath):
            shutil.rmtree(destinationPath)
        
        filesPath = os.path.join(".", "server-scripts")
        shutil.copytree(filesPath, destinationPath)
        
        shutil.copy(self._configuration.configPath(), destinationPath)
        
        filesPath = os.path.join(".", "configuration")
        shutil.copytree(filesPath, os.path.join(destinationPath, "configuration"))

    def _copyFiles(self, path):
        run([
            "sshpass",
            "-p",
            self._password,
            "scp",
            "-r",
            path,
            f"{self._username}@{self._host}:~/"
        ], check=True)

    def _reboot(self, user):
        logger.info("Rebooting")
        try:
            self._runSshCommand(f"export SUDO_ASKPASS=/home/{user}/do-configure/ask-pass-default.py; sudo --askpass shutdown -r now")
        except:
            pass #expected, ssh will terminate connection
        time.sleep(1)

    def _runSshCommand(self, command):
        run([
            "sshpass",
            "-p",
            self._password,
            "ssh",
            f"{self._username}@{self._host}",
            command
        ], check=True)
