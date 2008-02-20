// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
//  - Initial implementation
// *****************************************************************************
using System;
using System.Threading;
using System.Windows.Forms;
using nrobocodeui.dialog;
using robocode;
using robocode.ui;

namespace nrobocodeui.manager
{
    /// <summary>
    /// Proxy
    /// </summary>
    public class WindowManager : LoadableManagerBase, IWindowManager
    {
        private RobocodeFrame frame;
        private Thread battleWorker;
        private Robocode robocodeMain;

        public bool initializeDisplay()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            frame = new RobocodeFrame(RobocodeManager);

            return false;
        }

        public void runDisplay(Robocode robocode)
        {
            this.robocodeMain = robocode;
            try
            {
                //will block until end of game
                Application.Run(frame);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                Console.WriteLine(ex.StackTrace);
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
                /*if (robocodeMain.setup.securityOption)
                {
                    RobocodeSecurityManager rsm = java.lang.System.getSecurityManager() as RobocodeSecurityManager;
                    if (rsm != null)
                    {
                        rsm.addSafeThreadGroups();
                    }
                }*/
                Robocode.run(robocodeMain);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                throw;
            }
        }

        public IRobocodeFrame getRobocodeFrame()
        {
            return frame.FrameProxy;
        }

        public void showResultsDialog()
        {
        }

        private SplashScreen splashScreen = null;

        public void showSplashScreen()
        {
            splashScreen = new SplashScreen();
            splashScreen.Show(getRobocodeFrame() as Form);
            splashScreen.Update();

            RobocodeManager.getRobotRepositoryManager().getRobotRepository();

            RobocodeManager.getImageManager();
            RobocodeManager.getCpuManager().getCpuConstant();

            splashScreen.Close();
        }

        public void setStatus(string str)
        {
            if (splashScreen != null)
            {
                splashScreen.setStatus(str);
            }
            else
            {
                frame.FrameProxy.setStatus(str);
            }
        }

        public void showRobocodeFrame(bool b)
        {
            frame.FrameProxy.setIconified(!b);
        }

        public void messageError(string str)
        {
            MessageBox.Show(str, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        public void messageWarning(string str)
        {
            MessageBox.Show(str, "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
        }

        public static void HandleException(Exception ex)
        {
            Console.WriteLine(ex.ToString());
            Console.WriteLine(ex.StackTrace);
        }
    }
}