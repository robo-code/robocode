#!/bin/sh
pwd=`pwd`
cd ${0%/*}
java -Xmx512M -Dsun.io.useCanonCaches=false -cp ./libs/*: robocode.Robocode $*
cd $pwd
