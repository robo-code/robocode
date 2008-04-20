#!/bin/sh
olddir=`pwd` 
robohome=`dirname $0` 
echo "Using robohome $robohome" 
cd "$robohome" 
java -Xmx512M -Dsun.io.useCanonCaches=false -cp libs/robocode.jar:libs/codesize.jar:libs/cachecleaner.jar robocode.Robocode $*
echo "Goodbye!" 
cd "$olddir"