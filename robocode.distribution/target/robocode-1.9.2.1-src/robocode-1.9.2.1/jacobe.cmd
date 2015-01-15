@rem we expect jacobe.exe to be installed on system PATH
@echo off

SET JAVA_HOME=C:\Robocode\thoma\Downloads\jdk1.6.0_12
SET PATH=%PATH%;%JAVA_HOME%\bin 

if exist "%~dp0\tools\lib\jacobe.jar" goto jacobe
call "%~dp0\tools\loadTools.cmd"

:jacobe
call "%~dp0\tools\bin\ant.bat" -buildfile "%~dp0\tools\jacobe\build.xml"
