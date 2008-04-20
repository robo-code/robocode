#!/bin/sh
cd jikes-1.22
chmod a+rx configure
./configure
make
mkdir -p bin
cp src/jikes bin/