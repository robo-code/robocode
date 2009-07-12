@echo off

if exist "%~dp0\tools\lib\jni4net.j-0.2.0.0.jar" goto gen
call "%~dp0\tools\loadTools.cmd"

:gen
%~dp0\tools\lib\jni4net.proxygen-0.2.0.0.exe tools\proxygen\robocode.proxygen.xml

