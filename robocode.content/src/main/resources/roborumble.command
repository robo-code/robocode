#!/bin/sh
pwd=`pwd`
cd ${0%/*}
java -Xmx512M -cp libs/robocode.jar:libs/roborumble.jar:libs/codesize-1.1.jar roborumble.RoboRumbleAtHome ./roborumble/roborumble.txt
cd $pwd
