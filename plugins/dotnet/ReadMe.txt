This is guide to build Robocode for .NET

1) Build Java Robocode first with 'mvn install' from root directory (\robocode).

2) You need C# compiler and .NET SDK at least 4.0.
   http://www.microsoft.com/en-us/download/details.aspx?id=17851

   Best option is to install Visual Studio C# Express 2010 or newer.

3) Make sure you have csc.exe on PATH.

4) Make sure you have JAVA_HOME set to proper version of Java SDK (64/32 bit) depending on your system, i.e. Java 6 SDK or newer.

5) Install 'HTML Help 1 compiler' and afterward Sandcastle Help File Builder (SHFB):

   HTML Help 1 compiler:
     http://msdn.microsoft.com/en-us/library/ms669985(VS.85).aspx

   SHFB:
     http://shfb.codeplex.com/

   When installing the SHFB, you don't need to install HTML Help 2 as only version 1 is used for Robocode.

6) Run \plugins\dotnet\tools\loadTools.cmd 

7) Run \plugins\dotnet\tools\keys\gennetkey.cmd 

8) Run mvnassembly.cmd from \plugins\dotnet\ folder

9) Find the results in \robocode\plugins\dotnet\robocode.dotnet.distribution\target\robocode.dotnet-x.x.x.x-setup.jar
