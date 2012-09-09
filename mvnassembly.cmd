@echo off

if not exist "%~dp0\tools\lib\maven-2.2.1-uber.jar" (
   call "%~dp0\tools\loadTools.cmd"
)

call "%~dp0\tools\bin\mvn.bat" clean install ant:ant -DskipTests=false %*