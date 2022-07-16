from .installation.Installation import Installation

import json
import getopt
import logging
import os
import sys
import yaml

from enum import Enum

class Configuration:
    def __init__(self, arguments, currentPath=None):
        try:
            opts, args = getopt.getopt(
                arguments,
                "c:l:j:",
                [
                    "configuration=",
                    "log=",
                    "jobs="
                ]
            )
        except getopt.GetoptError:
            logging.error(f"Invalid arguments given")
            sys.exit(2)

        logLevel="ERROR"
        jobsToRun=[]

        if currentPath == None:
            self._currentPath = os.path.dirname(__file__)
        else:
            self._currentPath = currentPath

        configPath = os.path.join(self._currentPath, "..", "ldap-config-new.yaml")
        
        for opt,arg in opts:
            if opt in ("-l", "--log"):
                logLevel = arg
            elif opt in ("-c", "--configuration"):
                configPath = arg
            elif opt in ("-j", "--jobs"):
                jobsToRun = list(map(lambda s: s.strip(),arg.split(",")))

        numericLogLevel = getattr(logging, logLevel.upper(), None)
        if not isinstance(numericLogLevel, int):
            raise ValueError('Invalid log level: %s' % loglevel)
        self._logLevel = numericLogLevel

        with open(configPath, "r") as file:
            yamlData = yaml.safe_load(file)

        self._installation = Installation(yamlData["installation"])
        self._jobsToRun = jobsToRun

        if len(jobsToRun) == 0:
            self._jobRunRule = JobRunRule.ALL
        else:
            self._jobRunRule = JobRunRule.SELECTED


    def installation(self):
        return self._installation

    def jobRunRule(self):
        return self._jobRunRule

    def jobsToRun(self):
        return self._jobsToRun

    def toJson(self):
        def _toDict(o):
            if isinstance(o, frozenset):
                dict(o).__dict__
            else:
                o.__dict__
        
        return json.dumps(self, default=_toDict)
    
    def __str__(self):
        return self.toJson()


class JobRunRule(Enum):
    ALL = 1
    SELECTED = 2
