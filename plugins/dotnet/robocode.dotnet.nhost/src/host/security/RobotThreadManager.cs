using System;
using System.Collections.Generic;
using System.Text;
using net.sf.robocode.dotnet.host.proxies;

namespace net.sf.robocode.dotnet.host.security
{
    internal class RobotThreadManager
    {
        public RobotThreadManager(HostingRobotProxy proxy)
        {
            
        }

        public bool waitForStop()
        {
            return false;//TODO
        }
        public bool forceStop()
        {
            return false;//TODO
        }

        public void start()
        {
        }

        public void cleanup()
        {
            
        }
    }
}
