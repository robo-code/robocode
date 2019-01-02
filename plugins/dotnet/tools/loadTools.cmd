@REM
@REM Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM https://robocode.sourceforge.io/license/epl-v10.html
@REM

@echo off

if not exist "%~dp0/lib/" (
   mkdir "%~dp0/lib/"
)

java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.n/0.8.7.0/ "%~dp0/lib/" jni4net.n-0.8.7.0.dll
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.n.w32.v20/0.8.7.0/ "%~dp0/lib/" jni4net.n.w32.v20-0.8.7.0.dll
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.n.w64.v20/0.8.7.0/ "%~dp0/lib/" jni4net.n.w64.v20-0.8.7.0.dll
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.n.w32.v40/0.8.7.0/ "%~dp0/lib/" jni4net.n.w32.v40-0.8.7.0.dll
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.n.w64.v40/0.8.7.0/ "%~dp0/lib/" jni4net.n.w64.v40-0.8.7.0.dll
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/jni4net.j/0.8.7.0/ "%~dp0/lib/" jni4net.j-0.8.7.0.jar
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/net/sf/jni4net/proxygen/0.8.7.0/ "%~dp0/lib/" proxygen.exe
java -cp "%~dp0/../../../tools/loader" Loader https://robocode.sourceforge.io/mvnrepo/org/nunit/nunit.framework/2.4.3.0/ "%~dp0/lib/" nunit.framework-2.4.3.0.dll

