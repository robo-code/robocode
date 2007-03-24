#!/bin/sh
olddir=`pwd` 
robohome=`dirname $0` 
echo "Using robohome $robohome" 
cd "$robohome" 
java -Xmx512M -Dsun.io.useCanonCaches=false -jar libs/robocode.jar:libs/codesize.jar
echo "Goodbye!" 
cd "$olddir"