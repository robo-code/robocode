@echo off

rem SET JAVA_HOME=C:\Robocode\thoma\Downloads\jdk1.6.0_12
rem SET PATH=%PATH%;%JAVA_HOME%\bin 

mvn clean install ant:ant -DskipTests=true %*
rem mvn eclipse:eclipse
