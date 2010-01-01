@echo off
rem if exist "%~dp0\..\..\tools\lib\maven-2.0.9-uber.jar" goto mvn
rem call "%~dp0\..\..\tools\loadTools.cmd"
rem :mvn
rem @call "%~dp0\..\..\tools\bin\mvn.bat" package assembly:assembly -Dmaven.test.skip=false
mvn install assembly:assembly -Dmaven.test.skip=false