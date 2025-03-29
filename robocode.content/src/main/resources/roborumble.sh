#!/bin/sh
#
# Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

#----------------------------------------------------------
# RoboRumble
#----------------------------------------------------------

# Used for setting Java options
. ./set_java_options.sh
exit_code=$?

# Save present work directory (pwd)
pwd=$(pwd)

# Change directory to the directory where this script is located
cd "${0%/*}" || exit

if [ "$exit_code" -ne 100 ]; then
  # Run RoboRumble
  java \
    -cp "libs/*" \
    -Xmx512M \
    -Xdock:name=RoboRumble \
    -Xdock:icon=roborumble.ico \
    -XX:+IgnoreUnrecognizedVMOptions \
    "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" \
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" \
    roborumble.RoboRumbleAtHome \
    ./roborumble/roborumble.txt
fi

# Restore present work directory
cd "${pwd}" || exit
