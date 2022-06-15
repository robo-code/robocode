#!/bin/sh
################################################################################
# Copyright (c) 2001-2021 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
################################################################################

pwd=`pwd`
cd "${0%/*}"
# Older than Java 12
# java -Xdock:icon=robocode.ico -Xdock:name=Robocode -Xmx512M -cp "libs/*" -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" robocode.Robocode $*

# Java 12 and newer
java -Xdock:icon=robocode.ico -Xdock:name=Robocode -Xmx512M -cp "libs/*" -Djava.security.manager=allow -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" robocode.Robocode $*
cd "${pwd}"
