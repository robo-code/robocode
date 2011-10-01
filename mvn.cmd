@echo off
if exist "%~dp0\tools\lib\maven-*-uber.jar" goto mvn
call "%~dp0\tools\loadTools.cmd"

:mvn
@call "%~dp0\tools\bin\mvn.bat" %*