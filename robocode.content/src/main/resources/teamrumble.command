#!/bin/sh
################################################################################
# Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
################################################################################


#----------------------------------------------------------
# Run Robocode
#----------------------------------------------------------

# IMPORTANT: Take notice of which version of Java you are using!
#
# When running Robocode on Java 12 or never, the -Djava.security.manager=allow must be set by the
# user explicitly to enable the Java Security Manager, which Robocode required.
#
# Read more about this option here:
# https://openjdk.org/jeps/411
#
# If you are running a Java version older than Java 12, uncomment or remove the "export _JAVA_OPTIONS"
# line. You find the java version by writing "java -version" in a terminal.

# Uncomment or remove this line, if you run on a Java version older than version 12
export _JAVA_OPTIONS="-Djava.security.manager=allow"

# Save present work directory (pwd)
pwd=`pwd`

# Change directory to the directory where this script is located
cd "${0%/*}"

# Run Robocode with Java
java -Xdock:icon=roborumble.ico -Xdock:name=TeamRumble -Xmx512M -cp "libs/*" -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" roborumble.RoboRumbleAtHome ./roborumble/teamrumble.txt

# Restore present work directory
cd "${pwd}"
