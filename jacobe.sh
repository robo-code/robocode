#!/bin/bash

# we expect jacobe to be installed on PATH

if [ -f ./tools/lib/jacobe.jar ]; then
   export CLASSPATH=$CLASSPATH:./tools/lib/ant-launcher.jar:./tools/lib/ant.jar:./tools/lib/jacobe.jar
   ./tools/bin/ant.sh -buildfile ./tools/jacobe/build.xml
else
   ./tools/loadTools.sh
fi
