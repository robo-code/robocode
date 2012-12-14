@echo off

call mvn clean install ant:ant eclipse:eclipse -DskipTests=false %*