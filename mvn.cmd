@echo off

if not exist "%~dp0\tools\lib\maven-*-uber.jar" (
   call "%~dp0\tools\loadTools.cmd"
)

call "%~dp0\tools\bin\mvn.bat" %*