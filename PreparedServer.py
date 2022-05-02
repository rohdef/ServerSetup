import logging
import socket
import time

from subprocess import run

class PreparedServer:
    def __init__(self, configuration):
        self._configuration = configuration
        
        identity = configuration.autoInstall().identity()
        self._host = configuration.clientHost()
        self._username = identity.username()
        self._password = identity.password()

    def setupServer(self):
        logging.info("Waiting for SSH to be ready")
        self._awaitConnectionReady()

        logging.info("Executing setup scripts")
        self._runSshCommand("~/do-configure/setup.sh")

        # self._reboot()

        logging.info("Done initializing ready to perform actions on the server")

    def _awaitConnectionReady(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(2)

        for x in range(60):
            logging.info(f"Testing for conntection, attemt: {x}")
            
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                if s.connect_ex((self._host, 22)) == 0:
                    return None
                else:
                    time.sleep(1)

        raise Exception("Could not contact server")

    def _reboot(self):
        logging.info("Rebooting")
        self._runSshCommand("export SUDO_ASKPASS=/home/ubuntu/do-configure/ask-pass-new.py; sudo --askpass shutdown -r now || true")
        time.sleep(1)
    
    def _runSshCommand(self, command):
        run([
            "ssh",
            f"{self._username}@{self._host}",
            command
        ], check=True)
