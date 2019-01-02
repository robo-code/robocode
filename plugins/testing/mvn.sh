#!/bin/bash
#
# Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#

dp0=${0%/*}

if [ ! -d "$dp0/tools/lib" ]; then
   mkdir $dp0/tools/lib
fi

if [ ! -f $dp0/../../tools/lib/maven-*-uber.jar ]; then
   $dp0/../../tools/loadTools.sh
fi

$dp0/../../mvnw $*