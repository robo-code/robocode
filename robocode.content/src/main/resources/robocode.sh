#!/bin/sh
#
# Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

#----------------------------------------------------------
# Run Robocode (UI)
#----------------------------------------------------------

# Used for setting Java options
. ./set_java_options.sh
exit_code=$?

# Save present work directory (pwd)
pwd=$(pwd)

# Change directory to the directory where this script is located
cd "${0%/*}" || exit

if [ "$exit_code" -ne 100 ]; then
  # Run Robocode
  java \
    -cp "libs/*" \
    -Xmx512M \
    -Xdock:name=Robocode \
    -Xdock:icon=robocode.ico \
    -XX:+IgnoreUnrecognizedVMOptions \
    "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" \
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" \
    "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED" \
    "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" \
    robocode.Robocode "$@"
fi

# Restore present work directory
cd "${pwd}" || exit
