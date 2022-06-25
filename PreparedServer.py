import logging
import socket
import time

from subprocess import run

from ProvisionedServer import ProvisionedServer

logger = logging.getLogger(__name__)

class PreparedServer:
    def __init__(self, configuration):
        self._configuration = configuration
        
        identity = configuration.autoInstall().identity()
        self._host = configuration.clientHost()
        self._username = identity.username()
        self._password = identity.password()

    def provision(self):
        logger.info("Waiting for SSH to be ready")
        self._awaitConnectionReady()

        logger.info("Executing setup scripts")
        self._runSshCommand("~/do-configure/provision.sh")

        self._reboot(self._username)

        logger.info("Done initializing ready to perform actions on the server")
        return ProvisionedServer(self._configuration)

    def _awaitConnectionReady(self):
        for x in range(60):
            logger.info(f"Testing for conntection, attemt: {x}")
            
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.settimeout(1)

                if s.connect_ex((self._host, 22)) == 0:
                    return None
                else:
                    time.sleep(1)

        raise Exception("Could not contact server")

    def _reboot(self, user):
        logging.info("Rebooting")
        try:
            self._runSshCommand(f"export SUDO_ASKPASS=/home/{user}/do-configure/ask-pass-new.py; sudo --askpass shutdown -r now")
        except:
            pass #expected, ssh will terminate connection
        time.sleep(1)
    
    def _runSshCommand(self, command):
        run([
            "ssh",
            f"{self._username}@{self._host}",
            command
        ], check=True)
