@echo off

mvn clean install ant:ant -DskipTests=false %*
rem mvn eclipse:eclipse