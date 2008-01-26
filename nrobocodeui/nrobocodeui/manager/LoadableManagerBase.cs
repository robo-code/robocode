using System;
using robocode.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public abstract class LoadableManagerBase : ILoadableManager
    {
        public RobocodeManager RobocodeManager
        {
            get
            {
                return robocodeManager;
            }
        }
        private RobocodeManager robocodeManager;
        public virtual void setRobocodeManager(RobocodeManager rm)
        {
            Console.WriteLine("setRobocodeManager");
            robocodeManager = rm;
        }
    }
}
