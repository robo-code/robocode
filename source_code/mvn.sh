#!/bin/bash

dp0=${0%/*}

if [ ! -f $dp0/tools/lib/maven-*-uber.jar ]; then
   $dp0/tools/loadTools.sh
fi

$dp0/tools/bin/mvn.sh $*

