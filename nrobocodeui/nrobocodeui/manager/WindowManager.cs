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
        private RobocodeFrameStub frame;

        public IRobocodeFrame getRobocodeFrame()
        {
            //TODO ZAMO
            if (frame==null)
            {
                frame = new RobocodeFrameStub();
            }
            return frame;
        }

        public void showResultsDialog()
        {
            //TODO ZAMO
            Console.WriteLine("Results");
        }

        public void showSplashScreen()
        {
            //TODO ZAMO
            Console.WriteLine("Splash");
        }

        public void showRobocodeFrame(bool b)
        {
            //TODO ZAMO
        }
    }
}
