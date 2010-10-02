@echo off
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.n/0.8.0.0/ "%~dp0/lib/" jni4net.n-0.8.0.0.dll
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.n.w32.v20/0.8.0.0/ "%~dp0/lib/" jni4net.n.w32.v20-0.8.0.0.dll
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.n.w64.v20/0.8.0.0/ "%~dp0/lib/" jni4net.n.w64.v20-0.8.0.0.dll
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.n.w32.v40/0.8.0.0/ "%~dp0/lib/" jni4net.n.w32.v40-0.8.0.0.dll
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.n.w64.v40/0.8.0.0/ "%~dp0/lib/" jni4net.n.w64.v40-0.8.0.0.dll
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.j/0.8.0.0/ "%~dp0/lib/" jni4net.j-0.8.0.0.jar
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/net/sf/jni4net/jni4net.proxygen/0.8.0.0/ "%~dp0/lib/" jni4net.proxygen-0.8.0.0.exe
java -cp "%~dp0/../../../tools/loader" Loader http://jni4net.googlecode.com/svn/mvnrepo/org/nunit/nunit.framework/2.4.3.0/ "%~dp0/lib/" nunit.framework-2.4.3.0.dll

