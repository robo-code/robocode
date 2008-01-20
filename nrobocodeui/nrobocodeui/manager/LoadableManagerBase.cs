using System;
using robocode.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public abstract class LoadableManagerBase : ILoadableManager
    {
        private RobocodeManager robocodeManager;
        public void setRobocodeManager(RobocodeManager rm)
        {
            Console.WriteLine("setRobocodeManager");
            robocodeManager = rm;
        }
    }
}
