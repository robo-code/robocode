#!/bin/bash

if [ -f ./tools/lib/maven-*-uber.jar ]; then
   ./tools/bin/mvn.sh package assembly:assembly -Dmaven.test.skip=true
else
   ./tools/loadTools.sh
fi
