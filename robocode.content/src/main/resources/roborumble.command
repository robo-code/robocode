#!/bin/sh
################################################################################
# Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
################################################################################

pwd=`pwd`
cd "${0%/*}"
java -Xdock:icon=roborumble.ico -Xdock:name=RoboRumble -Xmx512M -cp libs/robocode.jar:libs/roborumble.jar:libs/bcel-6.2.jar:libs/codesize-1.2.jar -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" roborumble.RoboRumbleAtHome ./roborumble/roborumble.txt
cd "${pwd}"
