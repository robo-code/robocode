using System;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode.control;
using String=java.lang.String;

namespace robocode.dotnet.nhost.host
{
    internal class DotnetHost : IHost
    {
        public IHostingRobotProxy createRobotProxy(IHostManager par0, RobotSpecification par1, RobotStatics par2,
                                                   IRobotPeer par3)
        {
            throw new NotImplementedException();
        }

        public String[] getReferencedClasses(IRobotRepositoryItem par0)
        {
            throw new NotImplementedException();
        }

        public object getRobotType(IRobotRepositoryItem par0, bool par1, bool par2)
        {
            throw new NotImplementedException();
        }
    }
}
