#!/bin/env python3

import os
import yaml

if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    configPath = os.path.join(currentPath, "ldap-config.yaml")
    with open(configPath, "r") as file:
       template = yaml.safe_load(file)

    autoInstall = template["autoinstall"]
    identity = autoInstall["defaultIdentity"]

    print(identity["password"])
