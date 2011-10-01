#!/bin/bash

dp0=${0%/*}

if [ ! -f $dp0/tools/lib/maven-*-uber.jar ]; then
   $dp0/tools/loadTools.sh
fi

./mvn.sh clean package ant:ant -DskipTests=false $*