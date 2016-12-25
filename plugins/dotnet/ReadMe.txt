====
    Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    which accompanies this distribution, and is available at
    http://robocode.sourceforge.net/license/epl-v10.html
====

This is guide to build Robocode for .NET

REQUIRED TOOLS FOR BUILDING THE ROBOCODE .NET PLUG-IN
=====================================================

Microsoft C# Compiler and .NET SDK
----------------------------------
- You need C# compiler and .NET SDK at least 4.0:
  http://www.microsoft.com/en-us/download/details.aspx?id=17851

- Best option is to install Visual Studio C# Express 2010 or newer.

- NOTE: Make sure you have csc.exe version 3.5 on PATH.
Something like
c:\Windows\Microsoft.NET\Framework\v3.5\csc.exe 

Microsoft HTML Help 1 Compiler and Sandcastle Help File Builder
---------------------------------------------------------------
- 'HTML Help 1 compiler' must be installed on the system - required by the
  Sandcastle Help File Builder (SHFB)

  HTML Help 1 compiler:
  http://msdn.microsoft.com/en-us/library/ms669985(VS.85).aspx

- Sandcastle Help File Builder (SHFB) must be installed on the system.
  http://shfb.codeplex.com/

  NOTE: When installing the SHFB, you don't need to install HTML Help 2 as
       only version 1 is used for Robocode.

Java Development Kit (JDK) and JAVA_HOME path
---------------------------------------------
- Oracle JDK 6 or newer must be installed on the system:
  http://www.oracle.com/technetwork/java/javase/downloads

- NOTE: Make sure to install the proper version (32 or 64-bit) depending on the
        system.


HOW TO BUILD THE ROBOCODE .NET PLUG-IN
======================================

1) Build the regular part of Robocode first with 'mvn install'
  (or mvnassembly.cmd) from source code root directory of Robocode.

2) CD (change directory) to \plugins\dotnet

3) Run: tools\loadTools.cmd 

4) Run: tools\keys\gennetkey.cmd 

5) Run: mvnassembly.cmd (standing in the \plugins\dotnet directory)

9) Find the resulting distribution file for the Robocode .NET plug-in named 
   robocode.dotnet-x.x.x.x-setup.jar within this directory:
   \plugins\dotnet\robocode.dotnet.distribution\target\

   
HOW TO BUILD NEW PROXY CLASSES USING JNI4NET
============================================
Robocode uses Jni4net (http://jni4net.com/) to bridge the .NET and Java parts
of Robocode. Only the .NET plug-in is written for the Microsoft .NET platform.
The rest of Robocode is build for the Java platform.

When changing the public APIs or internal classes of Robocode it is important
that the corresponding .NET classes are updated as well. Some classes for the
.NET are handwritten. Other parts are auto-generated using the Jni4Net proxygen
tool (details are provided here: http://jni4net.com/).

In order to update all the auto-generated source files for the .NET plugin you
must:

1) CD (change directory) to \plugins\dotnet

2) Run: generateProxies.cmd

NOTE: You might need to remove the 'rem' and change the following lines inside
      the generateProxies.cmd in order to force the JAVA_HOME and PATH to the
	  correct Java version:

rem set JAVA_HOME=%JDK6_HOME%
rem set PATH=%JAVA_HOME%\bin;%PATH%