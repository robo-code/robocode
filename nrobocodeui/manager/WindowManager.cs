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
using robocode.security;
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

        public bool initializeDisplay()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            frame = new RobocodeFrame(RobocodeManager);
            frameProxy = new RobocodeFrameProxy(frame, frame);

            return false;
        }

        public void runDisplay(Robocode robocode)
        {
            this.robocode = robocode;
            try
            {
                //will block until end of game
                Application.Run(frame);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                throw;
            }
            
        }

        public void setLookAndFeel()
        {

        }

        public void OnDisplayLoaded()
        {
            battleWorker = new Thread(RunBattle);
            battleWorker.Start();
        }

        private void RunBattle()
        {
            try
            {
                /*if (robocode.setup.securityOption)
                {
                    RobocodeSecurityManager rsm = java.lang.System.getSecurityManager() as RobocodeSecurityManager;
                    if (rsm != null)
                    {
                        rsm.addSafeThreadGroups();
                    }
                }*/
                Robocode.run(robocode);
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.ToString());
                throw;
            }
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
