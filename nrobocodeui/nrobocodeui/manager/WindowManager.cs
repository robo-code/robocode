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
using System.Threading;
using System.Windows.Forms;
using nrobocodeui.dialog;
using nrobocodeui.manager;
using robocode;
using robocode.ui;

namespace nrobocodeui.manager
{
    /// <summary>
    /// Proxy
    /// </summary>
    public class WindowManager : LoadableManagerBase, IWindowManager
    {
        private RobocodeFrameProxy  frameProxy;
        private RobocodeFrame frame;
        private Thread battleWorker;
        private Robocode robocode;

        public bool initializeDisplay(Robocode robocode)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            this.robocode = robocode;
            frame = new RobocodeFrame(RobocodeManager);
            frameProxy = new RobocodeFrameProxy(frame);

            //will block
            Application.Run(frame);

            //after game
            return false;
        }

        public void OnDisplayLoaded()
        {
            battleWorker = new Thread(RunBattle);
            battleWorker.Start();
        }

        private void RunBattle()
        {
            Robocode.run(robocode);
        }

        public IRobocodeFrame getRobocodeFrame()
        {
            return frameProxy;
        }

        public void showResultsDialog()
        {
        }

        public void showSplashScreen()
        {
        }

        public void showRobocodeFrame(bool b)
        {
        }
    }
}
