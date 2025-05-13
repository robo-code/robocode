#!/bin/sh
#
# Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

# ---------------------------------------------------------
# Extract Java version
# ---------------------------------------------------------

# Extract Java version from `java -version` command
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

# Check if JAVA_VERSION contains a '+' and strip it
JAVA_VERSION_PART=$(echo "$JAVA_VERSION" | cut -d '+' -f 1)

# Extract Java major version from the resulting JAVA_VERSION_PART
JAVA_MAJOR_VERSION=$(echo "$JAVA_VERSION_PART" | cut -d '.' -f 1)
if [ "$JAVA_MAJOR_VERSION" = 1 ]; then
  JAVA_MAJOR_VERSION=$(echo "$JAVA_VERSION_PART" | cut -d '.' -f 2)
fi

export JAVA_VERSION
export JAVA_MAJOR_VERSION

# Print out Java version being used
echo Robocode is running on Java $JAVA_MAJOR_VERSION

# ------------------------------------------------------
# Check if Java version is greater than 23
# ------------------------------------------------------
if [ "$JAVA_MAJOR_VERSION" -gt 23 ]; then
    echo "Robocode does not support Java versions newer than version 23."
    exit 100
fi

# ---------------------------------------------------------
# Set Java options
# ---------------------------------------------------------

# Only set 'java.security.manager=allow' for Java version > 11
if [ "$JAVA_MAJOR_VERSION" -gt 11 ]
then
  export _JAVA_OPTIONS="-Djava.security.manager=allow"
fi
