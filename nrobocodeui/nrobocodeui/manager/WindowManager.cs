// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using nrobocodeui.dialog;
using nrobocodeui.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public class WindowManager : LoadableManagerBase, IWindowManager
    {
        private IRobocodeFrame frame;

        public IRobocodeFrame getRobocodeFrame()
        {
            //TODO ZAMO
            if (frame==null)
            {
                frame = new RobocodeFrameStub();
            }
            return frame;
        }

        public void setLookAndFeel()
        {
            //TODO
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
            RobocodeFrame frame = RobocodeManager.getWindowManager().getRobocodeFrame() as RobocodeFrame;
            //frame.Show();
        }
    }
}
