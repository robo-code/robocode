@echo off

mvn clean install ant:ant -DskipTests=true %*
rem mvn eclipse:eclipse