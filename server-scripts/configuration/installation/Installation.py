import json

def requireText(string):
    if not string or string.isspace():
        raise Exception("String must be set, but was empty")
    return string

class Installation:
    def __init__(self, yamlData):
        yamlJobs = yamlData["jobs"]

        self._jobs = {key: Job(value) for key, value in yamlJobs.items()}

    def jobs(self):
        return self._jobs

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__)

    def __str__(self):
        return self.toJson()


class Job:
    def __init__(self, yamlData):
        self._name = yamlData["name"]
        self._steps = list(map(lambda step: Step(step), yamlData["steps"]))

    def name(self):
        return self._name

    def steps(self):
        return self._steps

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__)

    def __str__(self):
        return self.toJson()

class Step:
    def __init__(self, yamlData):
        self._name = yamlData["name"]
        self._uses = yamlData["uses"]
        self._parameters = yamlData.get("parameters", {})

    def name(self):
        return self._name

    def uses(self):
        return self._uses

    def parameters(self):
        return self._parameters

    def toJson(self):
        def _toDict(o):
            if isinstance(o, frozenset):
                dict(o).__dict__
            else:
                o.__dict__
        
        return json.dumps(self, default=_toDict)

    def __str__(self):
        return self.toJson()
