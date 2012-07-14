#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xmx512M -cp libs/robocode.jar robocode.Robocode $*
cd "${pwd}"
