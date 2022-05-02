#!/bin/env bash

export SUDO_ASKPASS=~/do-configure/ask-pass-new.py
sudo --askpass ~/do-configure/setup.py --log=INFO
