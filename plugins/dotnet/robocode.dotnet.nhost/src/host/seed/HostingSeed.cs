﻿using System;
using System.Reflection;
using System.Threading;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.control;
using Object = java.lang.Object;

namespace net.sf.robocode.dotnet.host.seed
{
    public class HostingSeed : AppDomainSeed
    {
        private static IHostManager hostManager;
        private static IRobotPeer robotPeer;
        private static RobotStatics statics;
        private static IRobotRepositoryItem itemSpecification;
        private static RobotSpecification robotSpecification;

        public static void Construct()
        {
            hostManager = CopyProxy<IHostManager>((IntPtr) domain.GetData("hostManager"));
            robotPeer = CopyProxy<IRobotPeer>((IntPtr)domain.GetData("peer"));
            statics = CopyProxy<RobotStatics>((IntPtr)domain.GetData("statics"));
            robotSpecification = CopyProxy<RobotSpecification>((IntPtr)domain.GetData("specification"));
            itemSpecification = CopyProxy<IRobotRepositoryItem>((IntPtr)domain.GetData("item"));
        }

        private static T CopyProxy<T>(IntPtr robotFullName)
        {
            Object temp = new java.lang.Object();
            ((IJvmProxy)temp).Copy(JNIEnv.ThreadEnv,robotFullName, IHostManager_._class);
            return Bridge.Cast<T>(temp);
        }

        public static void StartRound()
        {
            var commands = (ExecCommands) domain.GetData("commands");
            var status = (RobotStatus) domain.GetData("status");

            var robotFullName = (string) domain.GetData("loadRobot");
            Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
            Type robotType = assembly.GetType(robotFullName, false);
            var robotThread = new Thread(RobotMain);
            robotThread.Start(robotType);
        }

        private static void RobotMain(object param)
        {
            var robotType = (Type) param;
        }

        public static void ForceStopThread()
        {
        }

        public static void WaitForStopThread()
        {
        }

        /*private static IHostingRobotProxy CreateProxy()
        {
            IHostingRobotProxy robotProxy;
            IRobotRepositoryItem specification = (IRobotRepositoryItem)HiddenAccess.getFileSpecification(robotSpecification);

            if (specification.isTeamRobot())
            {
                robotProxy = new TeamRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isAdvancedRobot())
            {
                robotProxy = new AdvancedRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isStandardRobot())
            {
                robotProxy = new StandardRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isJuniorRobot())
            {
                robotProxy = new JuniorRobotProxy(specification, hostManager, peer, statics);
            }
            else
            {
                throw new AccessViolationException("Unknown robot type");
            }
            return robotProxy;            
        }
            RobotClassLoader loader = null;

            try
            {
                loader = new RobotClassLoader(robotRepositoryItem);
                Type robotClass = loader.LoadRobotMainClass();

                return Reflection.CheckInterfaces(robotClass);
            }
            catch (Exception ex)
            {
                if (message)
                {
                    Logger.logError(robotRepositoryItem.getFullClassName() + ": Got an error with this class: " + ex);
                }
                return RobotType.INVALID;
            }
            finally
            {
                if (loader != null)
                {
                    loader.Cleanup();
                }
            }

 * 

 
  
 */
    }
}
