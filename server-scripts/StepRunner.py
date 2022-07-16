import logging
import os
import pwd
import re

from System import System

class StepRunner(System):
    def __init__(self):
        self._logger = logging.getLogger(__name__)

    def runStep(self, step, environment):
        self._logger.info(f"Running step: {step.name()}")
        
        runners = {
            "debug@v1": self.debug,
            "updateEnvironment@v1": self.updateEnvironment,
            "setHostname@v1": self.setHostname,
            "disableSnap@v1": self.disableSnap,
            "aptInstall@v1": self.aptInstall,
            "createUser@v1": self.createUser,
            "deleteUser@v1": self.deleteUser,
            "debconfSettings@v1": self.debconfSettings,
            "configureSsh@v1": self.configureSsh,
            "addAuthorizedKeys@v1": self.addAuthorizedKeys,
            "changeUserShell@v1": self.changeUserShell,
        }

        runner = runners.get(step.uses(), self.missingPlugin(step))

        environmentUpdates = runner(
            environment,
            self._parseEnvironmentVariables(step.parameters(), environment)
        )
        newEnvironment = environment.copy()

        if not environmentUpdates == None:
            if not isinstance(newEnvironment, dict):
                raise Exception(f"Environment returned from runner implementation for: [{step.uses()}] was not a dictionary, but {type(newEnvironment)}")

            for key, value in environmentUpdates.items():
                newEnvironment[key] = value

        return newEnvironment

    def _parseEnvironmentVariables(self, parameters, environment):
        newEnvironment = {}

        for key, value in parameters.items():
            newEnvironment[key] = self._parseEnvironmentValue(value, environment)

        return newEnvironment

    def debug(self, environment, parameters):
        self._logger.info("Debug information")
        self._logger.info(json.dumps(environment, indent=4))
        
        return environment

    def updateEnvironment(self, environment, parameters):
        self._logger.info("Updating environment variable")

        environmentUpdates = {}
        
        for key, value in parameters.items():
            environmentUpdates[key] = self._parseEnvironmentValue(value, environment)

        return environmentUpdates

    ### Simplistic parser
    ### only looks for $properties and $environment followed by ".{variableName}" where variableName must be in the dict
    ### No escapes
    ### Doesn't allow anything but letters for now
    def _parseEnvironmentValue(self, value, environment):
        if not isinstance(value, str):
            return value
        
        if "$environment" in value:
            variables = re.findall(r"\$environment\.(\w+)", value)

            for variable in variables:
                if not variable in environment:
                    raise Exception(f"Cound not find key [{variable}] in given environment.  Did you set the environment correctly? (Use debug to inspect the environment)")
                
                value = value.replace(f"$environment.{variable}", environment[variable])
        
        return value

    def setHostname(self, environment, parameters):
        hostname = parameters["hostname"]
        
        self._logger.info(f"Changing hostname to: {hostname}")
        self._run(["hostnamectl", "set-hostname", hostname])
        self._logger.info("\tHostname successfully updated")

        return environment

    def disableSnap(self, environment, parameters):
        self._logger.info("Disabling snap package manager")
        self.aptPurge(environment, {"packages": ["snapd"]})
        self._run(["rm", "-rf", "/snap", "/var/snap", "/var/lib/snapd"])

        return environment

    def aptPurge(self, environment, parameters):
        packages = parameters["packages"]
        
        self._logger.info(f"Purging packages: {packages}")
        purgeCommand = ["apt-get", "-y", "purge"] + packages
        self._run(purgeCommand)
        
        self._logger.info("Cleaning unused packages")
        self._run(["apt", "-y", "autoremove"])

        return environment

    def aptInstall(self, environment, parameters):
        self._logger.info("Updating package lists")
        self._run(["apt-get", "update"])
        
        packages = parameters["packages"]
        self._logger.info(f"Installing packages: {packages}")
        installCommand = ["apt-get", "-o", "DPkg::options::=--force-confmiss", "install", "-y"] + packages
        self._run(installCommand)

        return environment

    
    def configureSsh(self, environment, parameters):
        self._logger.info("Configuring SSH server")
        
        configFile = "/etc/ssh/sshd_config"

        passwordAuthentication = self._toYesNo(parameters.get("passwordAuthentication", True))
        permitRootLogin = self._toYesNo(parameters.get("permitRootLogin", False))

        passwordAuthenticationKey = "PasswordAuthentication"
        permitRootLoginKey = "PermitRootLogin"
        
        self._run(["sed", "-i", f"/{passwordAuthenticationKey}/d", configFile])
        self._run(["sed", "-i", f"/{permitRootLoginKey}/d", configFile])
        self._run(f"echo \"{passwordAuthenticationKey} {passwordAuthentication}\" >> {configFile}", True)
        self._run(f"echo \"{permitRootLoginKey} {permitRootLogin}\" >> {configFile}", True)

        self._run(["service", "ssh", "reload"])

        return environment

    def _toYesNo(self, boolean):
        if boolean:
            return "yes"
        else:
            return "no"

    def createUser(self, environment, parameters):
        self._logger.info("Creating new user")
        
        realName = parameters["realName"]
        username = parameters["username"]
        password = parameters["password"]

        self._logger.info(f"\tEnsuring user with username: \"{username}\"")
        try:
            pwd.getpwnam(username)
            self._logger.info("\tUser is already present")
        except KeyError:
            self._logger.info(f"\tCreating user with username: \"{username}\"")
            self._run(["useradd", "-m", "-d", f"/home/{username}", "-s", "/bin/bash", username])
        
        self._logger.info(f"\tUpdating password for user: \"{username}\"")
        self._run(f"bash -c 'echo \"{username}:{password}\" | chpasswd'", True)

        self._logger.info(f"\tUpdating real name to \"{realName}\" for user \"{username}\"")
        self._run(["usermod", "-c", realName, username])

        self._logger.info(f"\tUpdating to have sudo rights for user \"{username}\"")
        self._run(["usermod", "-aG", "sudo", username])

        return environment

    def addAuthorizedKeys(self, environment, parameters):
        self._logger.info("Adding authorized ssh keys")

        username = parameters["username"]
        rsaKeys = parameters["rsaKeys"]
        
        homeDirResult = self._runCapture(f"echo ~{username}", shell=True)
        homeDir = homeDirResult.stdout.decode().strip()

        sshDir = os.path.join(homeDir, ".ssh")
        self._runAsUser(username, f"mkdir -p {sshDir}")
        self._run(["chmod", "700", sshDir])
        
        authorizedKeysPath = os.path.join(sshDir, "authorized_keys")
        self._runAsUser(username, f"touch {authorizedKeysPath}")
        self._run(["chmod", "600", authorizedKeysPath])
        with open(authorizedKeysPath, "w") as keys:
            keys.writelines("%s\n" % key for key in rsaKeys)

        return environment

    def changeUserShell(self, environment, parameters):
        self._logger.info("Changing user shell")

        username = parameters["username"]
        shell = parameters["shell"]
       
        self._logger.info(f"Updating shell for {username} to {shell}")
        self._run(["chsh", "-s", shell, username])
        
        return environment

    def deleteUser(self, environment, parameters):        
        username = parameters["username"]

        homeDirResult = self._runCapture(f"echo ~{username}", True)
        homeDir = homeDirResult.stdout.decode().strip()
        
        self._logger.info(f"Deleting user: {username}")
        self._run(["userdel", username])
        self._run(["rm", "-rf", homeDir])

        return environment

    def debconfSettings(self, environment, parameters):
        package = parameters["package"]
        configuration = parameters["configuration"]

        lines = []
        for item in configuration:
            name = item["name"]
            configurationType = item["type"]
            value = item["value"]
            
            line = f"{package} {name} {configurationType} {value}"
            lines.append(line)
        
        selections = "\n".join(lines)
        echo = self._runPipe(["echo", "-e", selections])
        self._endPipe(["debconf-set-selections"], echo)

        return environment

    def missingPlugin(self, step):
        def reporter(environment, parameters):
            self._logger.error(f"Unknown runner specified: {step.uses()}")
            raise Exception(f"Unknown runner specified: {step.uses()}")

        return reporter
