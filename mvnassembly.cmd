@echo off
if exist "%~dp0\tools\lib\maven-2.2.1-uber.jar" goto mvn
call "%~dp0\tools\loadTools.cmd"

:mvn
@call "%~dp0\tools\bin\mvn.bat" clean install ant:ant -DskipTests=true %*