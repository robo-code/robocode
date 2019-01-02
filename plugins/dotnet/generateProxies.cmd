@REM
@REM Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM https://robocode.sourceforge.io/license/epl-v10.html
@REM

@echo off

rem set JAVA_HOME=%JDK6_HOME%
rem set PATH=%JAVA_HOME%\bin;%PATH%

set NET_FRAMEWORK_HOME=C:\Windows\Microsoft.NET\Framework\v3.5
set PATH=%NET_FRAMEWORK_HOME%;%PATH%

if exist "%~dp0\tools\lib\proxygen.exe" goto gen
call "%~dp0\tools\loadTools.cmd"

:gen
"%~dp0\tools\lib\proxygen.exe" tools\proxygen\robocode.control.proxygen.xml
"%~dp0\tools\lib\proxygen.exe" tools\proxygen\robocode.proxygen.xml

if exist "%~dp0\robocode.dotnet.nhost\target\robocode.dotnet.nhost.dll" goto gen1
echo cat't find robocode.dotnet.nhost\target\robocode.dotnet.nhost.dll, please compile it
goto end

:gen1
if exist "%~dp0\tools\lib\robocode.dll" goto gen3
if exist "%~dp0\robocode.dotnet.api\target\robocode.dll" goto gen2
echo cat't find \robocode.dotnet.api\target\robocode.dll, please compile it
goto end

:gen2

copy "%~dp0\robocode.dotnet.api\target\robocode.dll" "%~dp0\tools\lib"

:gen3
"%~dp0\tools\lib\proxygen.exe" tools\proxygen\robocode.proxygen.net.xml

:end