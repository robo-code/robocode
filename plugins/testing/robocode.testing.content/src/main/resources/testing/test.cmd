@REM
@REM Copyright (c) 2001-2018 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://robocode.sourceforge.net/license/epl-v10.html
@REM

@echo off
java -Drobocode.home=.. -cp ../libs/junit-4.5.jar;../libs/robocode.jar;../libs/robocode.testing.jar;.; org.junit.runner.JUnitCore sample.TestWallBehavior
