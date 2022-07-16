import json
import logging
import os
import re
import socket
import time

from subprocess import run

class StepRunner:
    def __init__(self, configuration):
        self._logger = logging.getLogger(__name__)
        self._configuration = configuration
        self._scriptPath = "server-scripts"

    def runStep(self, step, environment):
        self._logger.info(f"Running step: {step.name()}")

        runners = {
            "debug@v1": self.debug,
            "updateEnvironment@v1": self.updateEnvironment,
            "installRecipeRunner@v1": self.installRecipeRunner,
            "runRecipe@v1": self.runRecipe,
            "reboot@v1": self.reboot,
        }

        runner = runners.get(step.uses(), self.missingPlugin(step))

        environmentUpdates = runner(environment, self._parseEnvironmentVariables(step.parameters(), environment))
        newEnvironment = environment.copy()

        if not environmentUpdates == None:
            if not isinstance(environmentUpdates, dict):
                raise Exception(f"Environment updates returned from runner implementation for: [{step.uses()}] was not a dictionary, but {type(newEnvironment)}")

            for key, value in environmentUpdates.items():
                newEnvironment[key] = value

        return newEnvironment

    def debug(self, environment, parameters):
        self._logger.info("Debug information")
        self._logger.info(json.dumps(environment, indent=4))

    def updateEnvironment(self, environment, parameters):
        self._logger.info("Updating environment variables")

        environmentUpdates = {}

        for key, value in parameters.items():
            environmentUpdates[key] = self._parseEnvironmentValue(value, dict(self._configuration.properties()), environment)

        return environmentUpdates

    def _parseEnvironmentVariables(self, parameters, environment):
        newEnvironment = {}

        for key, value in parameters.items():
            newEnvironment[key] = self._parseEnvironmentValue(
                value,
                dict(self._configuration.properties()),
                environment
            )

        return newEnvironment

    ### Simplistic parser
    ### only looks for $properties and $environment followed by ".{variableName}" where variableName must be in the dict
    ### No escapes
    ### Doesn't allow anything but letters for now
    def _parseEnvironmentValue(self, value, properties, environment):
        if not isinstance(value, str):
            return value

        if "$properties" in value:
            variables = re.findall(r"\$properties\.(\w+)", value)

            for variable in variables:
                if not variable in properties:
                    raise Exception(f"Cound not find key [{variable}] in given properties.  Did you pass all properties via `--property=key=value` or `-p key=value`?")

                value = value.replace(f"$properties.{variable}", properties[variable])

        if "$environment" in value:
            variables = re.findall(r"\$environment\.(\w+)", value)

            for variable in variables:
                if not variable in environment:
                    raise Exception(f"Cound not find key [{variable}] in given environment.  Did you set the environment correctly? (Use debug to inspect the environment)")

                value = value.replace(f"$environment.{variable}", environment[variable])

        return value

    def installRecipeRunner(self, environment, parameters):
        self._logger.info("Installing recipe runner")

        username = environment["sshUsername"]
        password = environment["sshPassword"]
        hostname = environment["sshHostname"]

        self._runSshCommand(
            environment,
            f"""echo "{password}" > /home/{username}/{self._scriptPath}/password || true"""
        )
        self._runSshCommand(
            environment,
            f"export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass rm -r /home/{username}/{self._scriptPath} || true"
        )
        self._copyFiles(environment, "server-scripts")
        self._runSshCommand(
            environment,
            f"""echo "{password}" > /home/{username}/{self._scriptPath}/password"""
        )

    def runRecipe(self, environment, parameters):
        self._logger.info("Running recipe")

        username = environment["sshUsername"]
        script = parameters["script"]

        path = os.path.join("recipes", "remote", f"{script}.yaml")
        self._copyFiles(environment, path)

        jobCommand = ""
        if "jobs" in parameters:
            jobs = parameters["jobs"]
            jobList = ",".join(jobs)
            jobCommand = f"-j {jobList}"

        self._runSshCommand(
            environment,
            f"export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass ~/{self._scriptPath}/declarative.py -c ~/{script}.yaml {jobCommand}"
        )

    def reboot(self, environment, parameters):
        self._logger.info("Rebooting remote system")

        username = environment["sshUsername"]

        try:
            self._runSshCommand(
                environment,
                f"export SUDO_ASKPASS=/home/{username}/{self._scriptPath}/ask-pass.py; sudo --askpass shutdown -r now"
            )
        except:
            pass #expected, ssh will terminate connection
        time.sleep(1)

        properties = dict(self._configuration.properties())
        awaitConnection = parameters.get("wait", False)
        if awaitConnection:
            self._logger.info("Waiting for connection")
            self._awaitConnectionReady(environment.get("hostname", properties["hostname"]))

    def _awaitConnectionReady(self, hostname):
        for x in range(60):
            self._logger.info(f"Testing for conntection, attemt: {x}")
            
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.settimeout(1)

                if s.connect_ex((hostname, 22)) == 0:
                    return None
                else:
                    time.sleep(1)

        raise Exception("Could not contact server")

    def _copyFiles(self, environment, path):
        username = environment["sshUsername"]
        hostname = environment["sshHostname"]
        
        run([
            "sshpass",
            "-p",
            environment["sshPassword"],
            "scp",
            "-r",
            path,
            f"{username}@{hostname}:~/"
        ], check=True)

    def _runSshCommand(self, environment, command):
        username = environment["sshUsername"]
        hostname = environment["sshHostname"]
        
        run([
            "sshpass",
            "-p",
            environment["sshPassword"],
            "ssh",
            f"{username}@{hostname}",
            command
        ], check=True)

    def missingPlugin(self, step):
        def reporter(environent, parameters):
            self._logger.error(f"Unknown runner specified: {step.uses()}")
            raise Exception(f"Unknown runner specified: {step.uses()}")

        return reporter
