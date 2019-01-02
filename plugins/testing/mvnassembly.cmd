@REM
@REM Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM https://robocode.sourceforge.io/license/epl-v10.html
@REM

@echo off

mvn clean install ant:ant -DskipTests=false %*
rem mvn eclipse:eclipse