import logging
import os
import sys

from System import System

class PreparedServer(System):
    def __init__(self, configuration):
        self._configuration = configuration

    def setup(self):
        logging.info("Pretending to do setup")
        pass
    
    def _installBasePackages(self, packages):
        # - mc
        # - fish
        # - vim
        # - aptitude
        # - htop
        # - python3-yaml
        pass


if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    configuration = HostConfiguration(sys.argv[1:], currentPath)

    logging.basicConfig(level=configuration.logLevel())
    
    server = PreparedServer(configuration)
    server.setup()
