#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xdock:icon=roborumble.ico -Xdock:name=MeleeRumble -Xmx1024M -cp libs/robocode.jar:libs/roborumble.jar:libs/codesize-1.1.jar roborumble.RoboRumbleAtHome ./roborumble/meleerumble.txt
cd "${pwd}"
