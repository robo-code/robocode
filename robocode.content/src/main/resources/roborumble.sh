#!/bin/sh
pwd=`pwd`
cd ${0%/*}
java -Xmx256M -Dsun.io.useCanonCaches=false -cp libs/*:config roborumble.RoboRumbleAtHome ./roborumble/roborumble.txt
cd $pwd
