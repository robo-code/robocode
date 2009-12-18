using System;
using System.Collections.Generic;
using System.Text;
using net.sf.robocode.host;

namespace net.sf.robocode.dotnet.host.security
{
    public class RobotFileSystemManager
    {
        public RobotFileSystemManager(long maxQuota, string writableRootDirectory, string readableRootDirectory, string rootFile)
        {
        }

        public int getMaxQuota()
        {
            return 1000000;
        }

        public int getQuotaUsed()
        {
            return 0;
        }
    }
}
