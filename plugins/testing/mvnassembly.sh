#!/bin/bash

dp0=${0%/*}

if [ ! -f "$dp0/tools/lib" ]; then
   ln -s $dp0/../../tools tools
fi

if [ ! -f $dp0/tools/lib/maven-*-uber.jar ]; then
   $dp0/tools/loadTools.sh
fi

$dp0/../../mvn.sh clean install ant:ant -DskipTests=false $*