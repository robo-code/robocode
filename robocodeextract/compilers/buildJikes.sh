#!/bin/sh
cd jikes-1.16
chmod a+rx configure
./configure
make
mkdir bin
cp src/jikes bin/