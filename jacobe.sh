#!/bin/bash
#
# Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

# NOTE: Here we expect the jacobe tools to be installed on PATH

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

