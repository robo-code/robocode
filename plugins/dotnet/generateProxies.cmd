@echo off

set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_22

if exist "%~dp0\tools\lib\jni4net.j-0.8.0.0.jar" goto gen
call "%~dp0\tools\loadTools.cmd"

:gen
"%~dp0\tools\lib\jni4net.proxygen-0.8.0.0.exe" tools\proxygen\robocode.proxygen.xml
"%~dp0\tools\lib\jni4net.proxygen-0.8.0.0.exe" tools\proxygen\robocode.control.proxygen.xml

