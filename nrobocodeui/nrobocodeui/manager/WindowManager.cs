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
            //TODO ZAMO
            if (frame==null)
            {
                frame = new RobocodeFrame();
            }
            return frame;
        }

        public void showResultsDialog()
        {
            //TODO ZAMO
        }

        public void showSplashScreen()
        {
            //TODO ZAMO
        }

        public void showRobocodeFrame(bool b)
        {
            //TODO ZAMO
        }
    }
}
