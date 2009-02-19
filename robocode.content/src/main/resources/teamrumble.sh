#!/bin/sh
pwd=`pwd`
cd ${0%/*}
java -Xmx128M -Dsun.io.useCanonCaches=false -cp libs/*:config roborumble.RoboRumbleAtHome ./roborumble/teamrumble.txt
cd $pwd
