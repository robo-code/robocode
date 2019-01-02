/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Globalization;
using System.Reflection;
using System.Security.Permissions;
using System.Threading;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.jni4net.utils;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.dotnet.nhost;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using Robocode;
using Thread = System.Threading.Thread;

namespace net.sf.robocode.dotnet.host.seed
{
    [ReflectionPermission(SecurityAction.Assert, Unrestricted = true)]
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    [SecurityPermission(SecurityAction.Assert, Unrestricted = true)]
    public class HostingSeed : AppDomainSeed
    {
        private static IHostManager hostManager;
        private static IRobotPeer robotPeer;
        private static RobotStatics statics;
        private static IRobotItem specification;
        private static Type robotType;
        private static Thread robotThread;
        private static HostingRobotProxy robotProxy;

        public static void Construct()
        {
            try
            {
                ModuleN.InitN();

                JNIEnv env = JNIEnv.ThreadEnv;
                JniGlobalHandle hmHandle = env.NewGlobalRef((JniHandle)domain.GetData("hostManager"));
                JniGlobalHandle peerHandle = env.NewGlobalRef((JniHandle)domain.GetData("peer"));
                JniGlobalHandle itemHandle = env.NewGlobalRef((JniHandle)domain.GetData("item"));
                hostManager = Bridge.CreateProxy<IHostManager>(hmHandle);
                robotPeer = Bridge.CreateProxy<IRobotPeer>(peerHandle);
                specification = Bridge.CreateProxy<IRobotItem>(itemHandle);

                statics = ((RobotStatics)domain.GetData("statics"));
                CreateProxy();

                Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
                Reflection.CheckAssembly(assembly);
                string robotFullName = specification.getFullClassName();
                robotType = assembly.GetType(robotFullName, false);
                robotProxy.setRobotType(robotType);
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        public static void StartRound()
        {
            try
            {
                var commands = (ExecCommands) domain.GetData("commands");
                var status = (RobotStatus) domain.GetData("status");

                robotProxy.initializeRound(commands, status);

                robotThread = new Thread(RobotMain);
                robotThread.CurrentCulture = CultureInfo.InvariantCulture;
                robotThread.CurrentUICulture = CultureInfo.InvariantCulture;
                robotThread.Start(null);
                robotThread.Name = "Robot: "+robotProxy.getStatics().getName();
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        private static void RobotMain(object param)
        {
            try
            {
                robotPeer.setupThread();
                robotProxy.run();
            }
            catch(Exception ex)
            {
                robotProxy.println(ex);
            }
            finally
            {
                JNIEnv.ThreadEnv.GetJavaVM().DetachCurrentThread();
            }
        }

        public static void ForceStopThread()
        {
            try
            {
                LoggerN.logMessage(HiddenAccessN.GetRobotName() +  " is not stopping.  Forcing a stop.");
                robotThread.Priority = ThreadPriority.Lowest;
                robotThread.Abort();
                robotThread.Interrupt();
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        public static void WaitForStopThread()
        {
            try
            {
                if (!robotThread.Join(1000))
                {
                    LoggerN.logError("Unable to stop thread for "+statics.getName());
                    robotPeer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
                    robotPeer.setRunning(false);
                }
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        public static void Cleanup()
        {
            try
            {
                robotProxy.cleanup();
            }
            catch (Exception ex)
            {
                LoggerN.logError(ex);
                throw;
            }
        }

        private static void CreateProxy()
        {
            if (specification.isTeamRobot())
            {
                robotProxy = new TeamRobotProxy(specification, hostManager, robotPeer, statics);
            }
            else if (specification.isAdvancedRobot())
            {
                robotProxy = new AdvancedRobotProxy(specification, hostManager, robotPeer, statics);
            }
            else if (specification.isStandardRobot())
            {
                robotProxy = new StandardRobotProxy(specification, hostManager, robotPeer, statics);
            }
            else if (specification.isJuniorRobot())
            {
                robotProxy = new JuniorRobotProxy(specification, hostManager, robotPeer, statics);
            }
            else
            {
                throw new AccessViolationException("Unknown robot type");
            }
        }
    }
}