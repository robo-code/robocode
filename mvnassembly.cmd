@echo off

call mvn clean install ant:ant -DskipTests=false %*