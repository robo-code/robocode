@echo off

if not exist "%~dp0\lib" (
   mkdir "%~dp0\lib"
)

java -cp "%~dp0/loader" Loader http://robocode.sourceforge.net/tools/libs/ "%~dp0/lib/" ant.jar ant-contrib-0.6.jar ant-launcher.jar jacobe.jar junit.jar classworlds-1.1.jar maven-2.2.1-uber.jar