#!/bin/bash

./mvn.sh clean install ant:ant -DskipTests=false $*
#./mvn.sh eclipse:eclipse