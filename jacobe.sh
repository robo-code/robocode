#!/bin/bash

# Here we expect the jacobe tools to be installed on PATH

dp0=${0%/*}

if [ ! -f $dp0/tools/lib/ant-launcher.jar ]; then
   $dp0/tools/loadTools.sh
elif [ ! -f $dp0/tools/lib/ant.jar ]; then
   $dp0/tools/loadTools.sh
elif [ ! -f $dp0/tools/lib/jacobe.jar ]; then
   $dp0/tools/loadTools.sh
fi

export CLASSPATH=$CLASSPATH:$dp0/tools/lib/ant-launcher.jar:$dp0/tools/lib/ant.jar:$dp0/tools/lib/jacobe.jar
$dp0/tools/bin/ant.sh -buildfile $dp0/tools/jacobe/build.xml

