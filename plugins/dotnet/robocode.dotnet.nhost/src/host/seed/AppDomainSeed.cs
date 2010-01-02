#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Reflection;
using System.Security.Permissions;
using java.lang;
using java.nio;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.jni4net.nio;
using net.sf.jni4net.utils;
using net.sf.robocode.core;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.manager;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.serialization;
using robocode.control;
using robocode.exception;
using Buffer = java.nio.Buffer;
using ByteBuffer = java.nio.ByteBuffer;
using Exception = System.Exception;
using StringBuilder = System.Text.StringBuilder;

namespace net.sf.robocode.dotnet.host.seed
{
    [ReflectionPermission(SecurityAction.Assert, Unrestricted = true)]
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    //[EnvironmentPermission(SecurityAction.Assert, Unrestricted = true)]
        //[SecurityPermission(SecurityAction.Assert, Unrestricted = true)]
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
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
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

        static void domain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            LoggerN.logError(e.ExceptionObject.ToString());
        }

        public static void Bind()
        {
            try
            {
                bool fullBind = (bool)domain.GetData("fullBind");
                var setup = new BridgeSetup(false);
                //setup.Verbose = true;
                //setup.Debug = true;
                setup.BindCoreOnly = true;
                setup.BindNative = false;
                setup.BindStatic = false;
                Bridge.CreateJVM(setup);
                JNIEnv env = JNIEnv.ThreadEnv;
                if (fullBind)
                {
                    Registry.RegisterType(typeof(ByteBuffer), true, env);
                    Registry.RegisterType(typeof(Buffer), true, env);
                    Registry.RegisterType(typeof(ByteBufferClr), true, env);
                    Registry.RegisterType(typeof(java.lang.System), true, env);
                    Registry.RegisterType(typeof(ByteOrder), true, env);
                    /*
                    Registry.RegisterType(typeof(RobotType), true, env);
                    Registry.RegisterType(typeof(Logger), true, env);

                    Registry.RegisterType(typeof(java.util.Random), true, env);
                    Registry.RegisterType(typeof(BadBehavior), true, env);
                    Registry.RegisterType(typeof(RandomFactory), true, env);
                    Registry.RegisterType(typeof(RobotSpecification), true, env);
                    Registry.RegisterType(typeof(ContainerBase), true, env);
                    Registry.RegisterType(typeof(RbSerializer), true, env);
                    Registry.RegisterType(typeof(Error), true, env);
                    Registry.RegisterType(typeof(AbortedException), true, env);
                    Registry.RegisterType(typeof(DeathException), true, env);
                    Registry.RegisterType(typeof(DisabledException), true, env);
                    Registry.RegisterType(typeof(WinException), true, env);

                    Registry.RegisterType(typeof(__IVersionManagerBase), true, env);
                    Registry.RegisterType(typeof(__IRobotPeer), true, env);
                    Registry.RegisterType(typeof(__IRobotStatics), true, env);
                    Registry.RegisterType(typeof(__IRobotRepositoryItem), true, env);
                    Registry.RegisterType(typeof(__ISerializableHelper), true, env);
                    Registry.RegisterType(typeof(__IHostManager), true, env);
                     */
                    Bridge.RegisterAssembly(typeof(RbSerializer).Assembly);
                }
                else
                {
                    Registry.RegisterType(typeof(RobotType), true, env);
                    Registry.RegisterType(typeof(Logger), true, env);
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
                    if (Reflection.CheckInterfaces(type) != RobotType.Invalid)
                    {
                        sb.Append("file://");
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
                Type robotType = assembly.GetType(robotFullName, false);
                if (robotType != null)
                {
                    domain.SetData("robotLoaded", Reflection.CheckInterfaces(robotType).getCode());
                }
            }
            catch (Exception ex)
            {
                domain.SetData("robotLoaded", 0);
                LoggerN.logError(ex);
                throw;
            }
        }
    }
}