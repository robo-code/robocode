#!/bin/bash
java -Xmx128M -Dsun.io.useCanonCaches=false -cp libs/robocode.jar:libs/codesize.jar:libs/roborumble.jar:roborumble.RoboRumbleAtHome ./roborumble/teamrumble.txt