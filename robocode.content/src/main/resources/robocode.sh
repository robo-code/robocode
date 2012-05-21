#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xmx512M -Dsun.java2d.opengl=True -cp libs/robocode.jar robocode.Robocode $*
cd "${pwd}"
