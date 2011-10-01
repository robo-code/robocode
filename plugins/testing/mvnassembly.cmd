@echo off
if exist "%~dp0\..\..\tools\lib\maven-2.2.1-uber.jar" goto mvn
call "%~dp0\..\..\tools\loadTools.cmd"

:mvn
..\..\mvn.cmd clean package ant:ant -DskipTests=false %*