#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xdock:icon=roborumble.ico -Xdock:name=TwinDuel -Xmx512M -cp libs/robocode.jar:libs/roborumble.jar:libs/codesize-1.1.jar roborumble.RoboRumbleAtHome ./roborumble/twinduel.txt
cd "${pwd}"
