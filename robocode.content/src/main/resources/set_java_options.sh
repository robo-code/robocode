#!/bin/sh
#
# Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

# ---------------------------------------------------------
# Extract Java version
# ---------------------------------------------------------

# Extract Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
JAVA_MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d "." -f 1)

# Extract Java major version
if [ "$JAVA_MAJOR_VERSION" = 1 ]
then
  JAVA_MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d "." -f 2)
fi

export JAVA_VERSION
export JAVA_MAJOR_VERSION

# Print out Java version being used
echo Robocode is running on Java $JAVA_MAJOR_VERSION

# ---------------------------------------------------------
# Set Java options
# ---------------------------------------------------------

# Only set 'java.security.manager=allow' for Java version > 11
if [ "$JAVA_MAJOR_VERSION" -gt 11 ]
then
  export _JAVA_OPTIONS="-Djava.security.manager=allow"
fi
