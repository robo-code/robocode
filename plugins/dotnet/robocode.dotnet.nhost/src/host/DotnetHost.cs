using System;
using System.Drawing;
using System.IO;
using System.Reflection;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode.control;
using String=java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    public class DotnetHost : IHost
    {
        public IHostingRobotProxy createRobotProxy(IHostManager hostManager, RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer)
        {
            return null;
        }

        public String[] getReferencedClasses(IRobotRepositoryItem par0)
        {
            return new String[] {};
        }

        public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, bool resolve, bool message)
        {
            string url = robotRepositoryItem.getFullUrl().ToString();
            string file = url.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                return null;
            }
            using (var shell = new AppDomainShell(file))
            {
                return shell.GetRobotType(robotRepositoryItem.getFullClassName());
            }

        }


    }
}
