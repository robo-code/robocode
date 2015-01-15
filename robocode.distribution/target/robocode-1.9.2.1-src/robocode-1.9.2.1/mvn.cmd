@echo off

SET JAVA_HOME=C:\Robocode\thoma\Downloads\jdk1.6.0_12
SET PATH=%PATH%;%JAVA_HOME%\bin 

if not exist "%~dp0\tools\lib\maven-*-uber.jar" (
   call "%~dp0\tools\loadTools.cmd"
)

call "%~dp0\tools\bin\mvn.bat" %*
