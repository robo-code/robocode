call "c:\Program Files\Scala\bin\scalac.bat" -cp ..\..\..\build\build\libs\robocode.jar MyFirstScalaRobot.scala -d ..\..\..\build\build\robots
copy MyFirstScalaRobot.properties ..\..\..\build\build\robots\samplescala
copy MyFirstScalaRobot.scala ..\..\..\build\build\robots\samplescala