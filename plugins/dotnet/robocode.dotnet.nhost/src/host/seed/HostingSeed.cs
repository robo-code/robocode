using System;
using System.Reflection;
using System.Threading;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.dotnet.nhost;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode;
using robocode.control;

namespace net.sf.robocode.dotnet.host.seed
{
    public class HostingSeed : AppDomainSeed
    {
        private static IHostManager hostManager;
        private static IRobotPeer robotPeer;
        private static RobotStatics statics;
        private static IRobotRepositoryItem specification;
        private static RobotSpecification robotSpecification;
        private static Type robotType;
        private static Thread robotThread;
        private static HostingRobotProxy robotProxy;

        public static void Construct()
        {
            ModuleN.InitN();

            hostManager = Bridge.CreateProxy<IHostManager>((IntPtr)domain.GetData("hostManager"));
            robotPeer = Bridge.CreateProxy<IRobotPeer>((IntPtr)domain.GetData("peer"));
            statics = ((RobotStatics)domain.GetData("statics"));
            robotSpecification = Bridge.CreateProxy<RobotSpecification>((IntPtr)domain.GetData("specification"));
            specification = Bridge.CreateProxy<IRobotRepositoryItem>((IntPtr)domain.GetData("item"));
            robotProxy = CreateProxy();

            Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
            string robotFullName = specification.getFullClassName();
            robotType = assembly.GetType(robotFullName, false);
            robotProxy.setRobotType(robotType);

        }

        public static void StartRound()
        {
            var commands = (ExecCommands) domain.GetData("commands");
            var status = (RobotStatus) domain.GetData("status");

            robotProxy.initializeRound(commands, status);

            robotThread = new Thread(RobotMain);
            robotThread.Start(null);
        }

        private static void RobotMain(object param)
        {
            robotProxy.run();
        }

        public static void ForceStopThread()
        {
            robotThread.Abort();
        }

        public static void WaitForStopThread()
        {
            if (!robotThread.Join(1000))
            {
                robotPeer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
                robotPeer.setRunning(false);
            }
        }

        private static HostingRobotProxy CreateProxy()
        {
            HostingRobotProxy robotProxy;

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
            return robotProxy;            
        }
    }
}
