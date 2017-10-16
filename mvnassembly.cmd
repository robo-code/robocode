@REM
@REM Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://robocode.sourceforge.net/license/epl-v10.html
@REM

@echo off

mvn clean install ant:ant -DskipTests=true %*
rem mvn eclipse:eclipse