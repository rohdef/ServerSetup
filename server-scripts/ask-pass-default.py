#!/bin/env python3

import os

if __name__ == "__main__":
    currentPath = os.path.dirname(__file__)
    configPath = os.path.join(currentPath, "password")
    with open(configPath, "r") as file:
       password = file.read()

    print(password)
