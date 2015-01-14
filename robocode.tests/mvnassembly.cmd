@echo off

SET JAVA_HOME=C:\Robocode\vorigeStudent\data\apps\jdk1.6.0_12
SET PATH=%PATH%;%JAVA_HOME%\bin 

mvn clean install ant:ant -DskipTests=true %*
rem mvn eclipse:eclipse