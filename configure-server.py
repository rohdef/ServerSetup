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
    # TODO blank-server should also update
    server = BlankServer(configuration)

    if False:
        preparedServer = server.initServer()

        logger.info("Provisioning server")
        provisionedServer = preparedServer.provision()
    else:
        logger.info("Starting from provisioned")

        from ProvisionedServer import ProvisionedServer
        provisionedServer = ProvisionedServer(configuration)

        server._prepareFiles("do-configure")
        server._runSshCommand(f"export SUDO_ASKPASS=/home/rohdef-admin/do-configure/ask-pass-new.py; sudo --askpass rm -r /home/rohdef-admin/do-configure || true")
        server._copyFiles("do-configure")

    logger.info("Performing server setup")
    provisionedServer.setup()

    shutil.rmtree("do-configure")
