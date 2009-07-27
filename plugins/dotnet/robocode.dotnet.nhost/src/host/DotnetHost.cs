using System;
using System.Drawing;
using System.Reflection;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.control;
using robocode.robotinterfaces;
using String=java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    public class DotnetHost : IHost
    {
        public IHostingRobotProxy createRobotProxy(IHostManager par0, RobotSpecification par1, RobotStatics par2,
                                                   IRobotPeer par3)
        {
            throw new NotImplementedException();
        }

        public String[] getReferencedClasses(IRobotRepositoryItem par0)
        {
            return new String[] {};
        }

        public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, bool resolve, bool message)
        {
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
        }


    }
}
