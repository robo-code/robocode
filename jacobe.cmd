@REM
@REM Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM https://robocode.sourceforge.io/license/epl-v10.html
@REM


@rem NOTE: Here we expect jacobe.exe to be installed on system PATH

@echo off
if exist "%~dp0\tools\lib\jacobe.jar" goto jacobe
call "%~dp0\tools\loadTools.cmd"

:jacobe
call "%~dp0\tools\bin\ant.bat" -buildfile "%~dp0\tools\jacobe\build.xml"