This is guide to build Robocode for .NET

1) build Java Robocode first with mvn install from root directory

2) You need C# compiler and .NET SDK at least 2.0. 
Best option is to install Visual Studio C# Express 2008
http://www.microsoft.com/express/Downloads/#2008-Visual-CS

3) Make sure you have csc.exe on PATH and also the absolute path to the root directory
   where mvn.cmd is located.

4) Make sure you have JAVA_HOME set to proper version of Java (64/32 bit)

5) Optionally install Sandcastle and SHFB and HTML Help 1 compiler.
 - http://www.codeplex.com/Sandcastle
 - http://www.codeplex.com/SHFB
 - http://msdn.microsoft.com/en-us/library/ms669985(VS.85).aspx
 - make sure that installation set SHFBROOT properly

6) run plugins\dotnet\tools\loadTools.cmd 

7) run plugins\dotnet\tools\keys\gennetkey.cmd 

8) run mvnassembly.cmd from \plugins\dotnet\ folder

9) find the results in \plugins\dotnet\target\robocode.dotnet-x.x.x.x-setup.jar

