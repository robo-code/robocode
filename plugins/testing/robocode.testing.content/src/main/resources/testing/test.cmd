@echo off
java -Drobocode.home=.. -cp ../libs/junit-4.5.jar;../libs/robocode.jar;../libs/robocode.testing.jar;.; org.junit.runner.JUnitCore sample.TestWallBehavior
