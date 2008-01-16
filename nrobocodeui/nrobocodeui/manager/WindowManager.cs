using System;
using System.Collections.Generic;
using System.Text;
using robocode.manager;
using nrobocodeui.dialog;
using nrobocodeui.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public class WindowManager : LoadableManagerBase, IWindowManager
    {
        private RobocodeFrame frame;

        public IRobocodeFrame getRobocodeFrame()
        {
            Console.WriteLine("getRobocodeFrame Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
            if (frame==null)
            {
                frame = new RobocodeFrame();
            }
            return frame;
        }

        public void showResultsDialog()
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
        }

        public void showSplashScreen()
        {
            Console.WriteLine("showSplashScreen Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
        }

        public void showRobocodeFrame(bool b)
        {
            Console.WriteLine("showRobocodeFrame Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
        }
    }
}
