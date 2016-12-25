@REM
@REM Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://robocode.sourceforge.net/license/epl-v10.html
@REM

@echo off

set PATH=c:\Windows\Microsoft.NET\Framework\v3.5\;%PATH%
mvn clean install ant:ant -DskipTests=false %*
rem mvn eclipse:eclipse