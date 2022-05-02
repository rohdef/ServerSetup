def requireText(string):
    if not string or string.isspace():
        raise Exception("String must be set, but was empty")
    return string

class AutoInstall:
    def __init__(self, yamlData):
        self._install = yamlData["autoinstall"]
        self._defaultIdentity = DefaultIdentity(self._install)
        self._identity = Identity(self._install)
        self._packages = Packages(self._install)

    def defaultIdentity(self):
        return self._defaultIdentity

    def identity(self):
        return self._identity

    def packages(self):
        return self._packages


class DefaultIdentity:
    def __init__(self, yamlData):
        self._identity = yamlData["defaultIdentity"]
        self._username = requireText(self._identity["username"])
        self._password = requireText(self._identity["password"])

    def username(self):
        return self._username

    def password(self):
        return self._password

class Identity:
    def __init__(self, yamlData):
        self._identity = yamlData["identity"]
        self._hostname = requireText(self._identity["hostname"])
        self._realName = requireText(self._identity["realname"])
        self._username = requireText(self._identity["username"])
        self._password = requireText(self._identity["password"])
        self._authorizedKeys = requireText(self._identity["authorized-keys"])

    def hostname(self):
        return self._hostname

    def realName(self):
        return self._realName

    def username(self):
        return self._username

    def password(self):
        return self._password

    def authorizedKeys(self):
        return self._authorizedKeys

class Packages:
    def __init__(self, yamlData):
        self._packages = yamlData["packages"]

    def packages(self):
        return self._packages
