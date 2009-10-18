using System;
using net.sf.jni4net;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode.control;
using Object=java.lang.Object;

namespace net.sf.robocode.dotnet.host.seed
{
    class HostingShell : AppDomainShell, IHostingRobotProxy
    {
        private IHostManager hostManager;
        private IRobotRepositoryItem specification;
        private RobotStatics statics;
        private IRobotPeer peer;
        public HostingShell(RobotSpecification robotSpecification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics, string dllFileName) 
        {
            Object s = HiddenAccess.getFileSpecification(robotSpecification);
            specification = Bridge.Cast<IRobotRepositoryItem>(s);
            this.hostManager = hostManager;
            this.peer = peer;
            this.statics = statics;
            Init(dllFileName);
        }

        public void startRound(Object par0, Object par1)
        {
        }

        public void forceStopThread()
        {
        }

        public void waitForStopThread()
        {
        }

        public void cleanup()
        {
            Dispose();
        }
    }
}
