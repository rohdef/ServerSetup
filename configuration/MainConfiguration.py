import getopt
import logging
import os
import sys
import yaml

from .installation.AutoInstall import AutoInstall

class MainConfiguration:
    def __init__(self, arguments, currentPath=None):
        try:
            opts, args = getopt.getopt(arguments, "c:l:h:", ["configuration=", "log=", "host="])
        except getopt.GetoptError:
            logging.error(f"Invalid arguments given")
            sys.exit(2)

        logLevel="ERROR"

        for opt,arg in opts:
            if opt in ("-l", "--log"):
                logLevel = arg
            elif opt in ("-h", "--host"):
                self._host = arg

        numericLogLevel = getattr(logging, logLevel.upper(), None)
        if not isinstance(numericLogLevel, int):
            raise ValueError('Invalid log level: %s' % loglevel)
        self._logLevel = numericLogLevel

        if currentPath == None:
            self._currentPath = os.path.dirname(__file__)
        else:
            self._currentPath = currentPath

        configPath = os.path.join(self._currentPath, "ldap-config.yaml")
        with open(configPath, "r") as file:
            yamlData = yaml.safe_load(file)
        self._autoInstall = AutoInstall(yamlData)
        self._configPath = configPath

    def autoInstall(self):
        return self._autoInstall

    def clientHost(self):
        return self._host

    def configPath(self):
        return self._configPath

    def currentPath(self):
        return self._currentPath
    
    def logLevel(self):
        return self._logLevel
