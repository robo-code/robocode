@rem we expect jacobe.exe to be installed on system PATH
@echo off
if exist "%~dp0\tools\lib\jacobe.jar" goto jacobe
call "%~dp0\tools\loadTools.cmd"

:jacobe
call "%~dp0\tools\bin\ant.bat" -buildfile "%~dp0\tools\jacobe\build.xml"