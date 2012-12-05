#!/bin/bash

dp0=${0%/*}

if [ ! -d "$dp0/tools/lib" ]; then
   mkdir $dp0/tools/lib
fi

if [ ! -f $dp0/../../tools/lib/maven-*-uber.jar ]; then
   $dp0/../../tools/loadTools.sh
fi

$dp0/../../mvn.sh $*