#!/bin/env python3

import logging
import logging.config
import os
import shutil
import sys

if __name__ == "__main__":
    logging.config.fileConfig("./logging.conf")
    logger = logging.getLogger(__name__)

    logger.info("Loading configuration")
    from configuration.MainConfiguration import MainConfiguration
    currentPath = os.path.dirname(__file__)
    configuration = MainConfiguration(sys.argv[1:], currentPath)

    logger.info("Initializing server")
    from BlankServer import BlankServer
    server = BlankServer(configuration)
    preparedServer = server.initServer()

    logger.info("Provisioning server")
    provisionedServer = preparedServer.provision()

    logger.info("Performing server setup")
    provisionedServer.setup()

    shutil.rmtree("do-configure")
