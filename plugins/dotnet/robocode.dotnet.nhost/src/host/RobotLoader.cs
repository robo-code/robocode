using System;
using net.sf.robocode.repository;

namespace net.sf.robocode.dotnet.host
{
    internal class RobotLoader
    {
        private IRobotRepositoryItem item;

        public RobotLoader(IRobotRepositoryItem item)
        {
            this.item = item;
        }

        public Type LoadRobotMainClass()
        {
            return null;
        }

        public void Cleanup()
        {
            
        }
    }
}
