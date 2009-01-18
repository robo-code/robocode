@echo off
if exist "%~dp0\tools\lib\maven-2.0.9-uber.jar" goto mvn
call "%~dp0\tools\loadTools.cmd"

:mvn
@call "%~dp0\tools\bin\mvn.bat" %*