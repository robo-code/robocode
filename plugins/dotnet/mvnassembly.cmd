@echo off
if exist "%~dp0\..\..\tools\lib\maven-2.2.1-uber.jar" goto mvn
call "%~dp0\..\..\tools\loadTools.cmd"
:mvn
if exist "%~dp0\tools\lib\nunit.framework-2.4.3.0.dll" goto mvn2
call "%~dp0\tools\loadTools.cmd"
:mvn2
@call "%~dp0\..\..\tools\bin\mvn.bat" clean install ant:ant -DskipTests=false %*