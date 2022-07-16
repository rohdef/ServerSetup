from subprocess import run, Popen, PIPE

class System:
    def _runCapture(self, command, shell=False):
        return run(command, shell=shell, capture_output=True)
    
    def _run(self, command, shell=False):
        run(command, check=True, shell=shell)

    def _runPipe(self, command, input=None):
        if input == None:
            process = Popen(command, stdout=PIPE)
        else:
            process = Popen(command, stdin=input.stdout, stdout=PIPE)
            input.stdout.close()

        return process

    def _endPipe(self, command, input):
        process = Popen(command, stdin=input.stdout)
        input.stdout.close()
        output = process.communicate()[0]

        if output != None:
            self._logger.info(output)
        else:
            self._logger.info("No output from command")

    def _runAsUser(self, user, command):
        run(f"sudo -u \"{user}\" {command}", check=True, shell=True)
