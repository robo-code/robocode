#!/bin/sh
pwd=`pwd`
cd ${0%/*}
java -Xmx512M -Dsun.io.useCanonCaches=false -cp libs/robocode.jar robocode.Robocode $*
cd $pwd
