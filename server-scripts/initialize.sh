#!/bin/env bash

export SUDO_ASKPASS=~/do-configure/ask-pass-default.py
sudo --askpass ~/do-configure/initialize.py --log=INFO
