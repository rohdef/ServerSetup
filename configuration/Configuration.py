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
                "c:j:l:h:p:",
                [
                    "configuration=",
                    "log=",
                    "hostname=",
                    "property=",
                    "jobs=",
                ]
            )
        except getopt.GetoptError as error:
            logging.error("Invalid argument given: %s", error.msg)
            sys.exit(2)

        logLevel="ERROR"
        jobsToRun=[]

        if currentPath == None:
            self._currentPath = os.path.dirname(__file__)
        else:
            self._currentPath = currentPath

        configPath = None

        properties = {}
        for opt,arg in opts:
            if opt in ("-l", "--log"):
                logLevel = arg
            elif opt in ("-c", "--configuration"):
                configPath = arg
            elif opt in ("-p", "--property"):
                property = arg.split("=")
                if len(property) > 2:
                    raise Exception(f"Misconfigured property only one `=` allowed, but found {len(property)} for [{arg}]")
                properties[property[0]] = property[1]
            elif opt in ("-j", "--jobs"):
                jobsToRun = list(map(lambda s: s.strip(),arg.split(",")))

        if configPath == None:
            raise Exception("Configuration path must be given")

        numericLogLevel = getattr(logging, logLevel.upper(), None)
        if not isinstance(numericLogLevel, int):
            raise ValueError('Invalid log level: %s' % loglevel)
        self._logLevel = numericLogLevel

        with open(configPath, "r") as file:
            yamlData = yaml.safe_load(file)

        self._installation = Installation(yamlData["installation"])
        self._properties = frozenset(properties.items())
        self._jobsToRun = jobsToRun

        if len(jobsToRun) == 0:
            self._jobRunRule = JobRunRule.ALL
        else:
            self._jobRunRule = JobRunRule.SELECTED


    def installation(self):
        return self._installation

    def properties(self):
        return self._properties

    def jobRunRule(self):
        return self._jobRunRule

    def jobsToRun(self):
        return self._jobsToRun

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__)
    
    def __str__(self):
        return self.toJson()


class JobRunRule(Enum):
    ALL = 1
    SELECTED = 2
