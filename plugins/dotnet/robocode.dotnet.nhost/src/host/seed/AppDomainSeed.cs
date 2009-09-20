using System;
using System.Diagnostics;
using System.Reflection;
using System.Text;
using System.Threading;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.host.proxies;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    public static class AppDomainSeed
    {
        private static string robotAssemblyFileName;
        private static string robotAssemblyShadowFileName;
        private static AppDomain domain;

        public static void Load(string[] args)
        {
            try
            {
                domain = AppDomain.CurrentDomain;
                robotAssemblyFileName = args[0];
                robotAssemblyShadowFileName = args[1];

                BridgeSetup setup=new BridgeSetup(false);
                setup.Verbose = true;
                setup.Debug = true;
                setup.BindNative = false;
                Bridge.CreateJVM(setup);
                Bridge.LoadAndRegisterAssembly(typeof (AppDomainSeed).Assembly.Location);
            }
            catch(Exception ex)
            {
                Debug.WriteLine(ex);
            }

        }

        public static void FindRobots()
        {
            var sb = new StringBuilder();
            Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
            foreach (Type type in assembly.GetTypes())
            {
                if (Reflection.CheckInterfaces(type)!=RobotType.Invalid)
                {
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


        public static void GetRobotType()
        {
            var robotFullName = (string)domain.GetData("loadRobot");
            Assembly assembly = Assembly.LoadFrom(robotAssemblyFileName);
            Type robotType = assembly.GetType(robotFullName, false);
            if (robotType != null)
            {
                domain.SetData("robotLoaded", Reflection.CheckInterfaces(robotType).getCode());
            }
        }

        public static void StartRound()
        {
            ExecCommands commands = (ExecCommands)domain.GetData("commands");
            RobotStatus status = (RobotStatus)domain.GetData("status");

            var robotFullName = (string)domain.GetData("loadRobot");
            Assembly assembly = Assembly.LoadFrom(robotAssemblyFileName);
            Type robotType = assembly.GetType(robotFullName, false);
            Thread robotThread = new Thread(RobotMain);
            robotThread.Start(robotType);

        }

        static void RobotMain(object param)
        {
            Type robotType = (Type) param;
        }

        public static void ForceStopThread()
        {
        }

        public static void WaitForStopThread()
        {
        }

        public static void Cleanup()
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
        }*/
    }
}

/*
 
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
