#!/bin/bash
#
# Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# https://robocode.sourceforge.io/license/epl-v10.html
#


dp0=${0%/*}

if [ ! -d "$dp0/lib" ]; then
   mkdir "$dp0/lib"
fi

if [ ! -e "$dp0/loader/Loader.class" ]; then
   javac "$dp0/loader/Loader.java"
fi

java -cp "$dp0/loader" Loader https://robocode.sourceforge.io/tools/libs/ "$dp0/lib/" ant.jar ant-contrib-0.6.jar ant-launcher.jar jacobe.jar junit.jar classworlds-1.1.jar maven-2.2.1-uber.jar
