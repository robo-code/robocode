#!/bin/bash

if [ -f ./tools/lib/maven-*-uber.jar ]; then
   ./tools/bin/mvn.sh $*
else
   ./tools/loadTools.sh
fi
