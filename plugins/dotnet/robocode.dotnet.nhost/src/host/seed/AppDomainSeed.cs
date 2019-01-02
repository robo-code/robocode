/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using java.nio;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.jni4net.nio;
using net.sf.jni4net.utils;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.io;
using net.sf.robocode.repository;
using net.sf.robocode.serialization;
using Buffer = java.nio.Buffer;
using ByteBuffer = java.nio.ByteBuffer;
using Exception = System.Exception;
using StringBuilder = System.Text.StringBuilder;
using robocode.control;

namespace net.sf.robocode.dotnet.host.seed
{
    [ReflectionPermission(SecurityAction.Assert, Unrestricted = true)]
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    public class AppDomainSeed
    {
        protected static string robotAssemblyFileName;
        protected static string robotAssemblyShadowFileName;
        protected static AppDomain domain;

        public static void Load(string[] args)
        {
            try
            {
                domain = AppDomain.CurrentDomain;
                domain.UnhandledException += (domain_UnhandledException);
                domain.AssemblyLoad += (domain_AssemblyLoad);
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        static void domain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            LoggerN.logError(e.ExceptionObject.ToString());
        }

        static void domain_AssemblyLoad(object sender, AssemblyLoadEventArgs args)
        {
            bool knownLocation = args.LoadedAssembly.Location != null &&
                                 robotAssemblyShadowFileName != null &&
                                 Path.GetFullPath(args.LoadedAssembly.Location).ToLower() ==
                                 Path.GetFullPath(robotAssemblyShadowFileName).ToLower();
            if (args.LoadedAssembly != typeof(Bridge).Assembly &&
                args.LoadedAssembly != typeof(AppDomainSeed).Assembly &&
                args.LoadedAssembly != typeof(RobocodeEngine).Assembly &&
                !knownLocation &&
                !args.LoadedAssembly.GlobalAssemblyCache)
            {
                string message = "dependent assemblies are not alowed" + args.LoadedAssembly.Location;
                LoggerN.logError(message);
                throw new SecurityException(message);
            }
        }

        public static void Open()
        {
            try
            {
                robotAssemblyFileName = (string)domain.GetData("robotAssemblyFileName");
                robotAssemblyShadowFileName = (string)domain.GetData("robotAssemblyShadowFileName");
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        public static void Bind()
        {
            try
            {
                bool fullBind = (bool)domain.GetData("fullBind");
                if (fullBind)
                {
                    var setup = new BridgeSetup(false);
                    setup.BindCoreOnly = true;
                    setup.BindNative = false;
                    setup.BindStatic = false;
                    setup.JavaHome = (string)domain.GetData("JavaHome");
                    Bridge.CreateJVM(setup);
                    JNIEnv env = JNIEnv.ThreadEnv;
                    Registry.RegisterType(typeof(ByteBuffer), true, env);
                    Registry.RegisterType(typeof(Buffer), true, env);
                    Registry.RegisterType(typeof(DirectByteBuffer), true, env);
                    Registry.RegisterType(typeof(java.lang.System), true, env);
                    Registry.RegisterType(typeof(ByteOrder), true, env);
                    Bridge.RegisterAssembly(typeof(RbSerializer).Assembly);
                }
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        public static void FindRobots()
        {
            try
            {
                var sb = new StringBuilder();
                Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
                foreach (Type type in assembly.GetTypes())
                {
                    if (Reflection.CheckInterfaces(type) != RobotTypeN.INVALID)
                    {
                        sb.Append("file:///");
                        sb.Append(robotAssemblyFileName);
                        sb.Append("!/");
                        sb.Append(type.FullName);
                        sb.Append(";");
                    }
                }
                if (sb.Length > 0)
                {
                    sb.Length--;
                }
                domain.SetData("robotsFound", sb.ToString());
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }


        public static void GetRobotType()
        {
            try
            {
                var robotFullName = (string) domain.GetData("loadRobot");
                Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
                Reflection.CheckAssembly(assembly);
                Type robotType = assembly.GetType(robotFullName, false);
                if (robotType != null)
                {
                    domain.SetData("robotLoaded", (int)Reflection.CheckInterfaces(robotType));
                }
            }
            catch (Exception ex)
            {
                domain.SetData("robotLoaded", 0);
                LoggerN.logError(ex);
            }
        }
    }
}