from subprocess import run

class System:
    def _runCapture(self, command):
        return run(command, shell=True, capture_output=True)
    
    def _run(self, command, shell=True):
        run(command, check=True, shell=shell)

    def _runAsUser(self, user, command):
        run(f"sudo --askpass -u {user} {command}", check=True, shell=True)
