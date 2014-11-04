::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
:: All rights reserved. This program and the accompanying materials
:: are made available under the terms of the Eclipse Public License v1.0
:: which accompanies this distribution, and is available at
:: http://robocode.sourceforge.net/license/epl-v10.html
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:: NOTE: Here we expect jacobe.exe to be installed on system PATH

@echo off
if exist "%~dp0\tools\lib\jacobe.jar" goto jacobe
call "%~dp0\tools\loadTools.cmd"

:jacobe
call "%~dp0\tools\bin\ant.bat" -buildfile "%~dp0\tools\jacobe\build.xml"