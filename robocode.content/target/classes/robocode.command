#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xdock:icon=robocode.ico -Xdock:name=Robocode -Xmx512M -cp libs/robocode.jar robocode.Robocode $*
cd "${pwd}"
