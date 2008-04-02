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

using System.ComponentModel;
using nrobocodeui.utils;
using robocode.peer;
using nrobocodeui.battleview;
using robocode.peer.proxies;
using robocode.ui;

namespace nrobocodeui.dialog
{

    public class RobocodeFrameProxy : IRobocodeFrame
    {
        public RobocodeFrameProxy(RobocodeFrame frame, ISynchronizeInvoke synchronizer)
        {
            this.frame = frame;
            this.synchronizer = synchronizer;
            closing = false;
        }

        private RobocodeFrame frame;
        private ISynchronizeInvoke synchronizer;
        private bool closing;

        public IBattleView getBattleView()
        {
            return frame.BattleViewProxy;
        }

        public void OnClosing()
        {
            ISynchronizeInvoke s = synchronizer;
            closing = true;
        }

        #region Block

        public string saveBattleDialog(string file)
        {
            if (closing)
                return file;
            //lock (synchronizer)
            {
                return synchronizer.Invoke(new Delegate<string, string>(frame.saveBattleDialog), new object[] {file}) as string;
            }
        }

        public void setIconified(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new Action<bool>(frame.setIconified), new object[] {value});
            }
        }

        public void validate()
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new Action(frame.validate), new object[] {});
            }
        }

        #endregion

        #region Send

        public void setStatus(string value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<string>(frame.setStatus), new object[] {value});
            }
        }

        public void setTitle(string value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<string>(frame.setTitle), new object[] {value});
            }
        }

        public void setEnableBattleSaveAsMenuItem(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<bool>(frame.setEnableBattleSaveAsMenuItem), new object[] {value});
            }
        }

        public void setEnableBattleSaveMenuItem(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<bool>(frame.setEnableBattleSaveMenuItem), new object[] {value});
            }
        }

        public void setEnableStopButton(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<bool>(frame.setEnableStopButton), new object[] {value});
            }
        }

        public void setEnableRestartButton(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<bool>(frame.setEnableRestartButton), new object[] {value});
            }
        }

        public void setEnableReplayButton(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<bool>(frame.setEnableReplayButton), new object[] {value});
            }
        }

        public void clearRobotButtons()
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action(frame.clearRobotButtons), new object[] {});
            }
        }

        public void addRobotButton(IRobotDialogManager irdm, IDisplayRobotProxy rp)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.BeginInvoke(new Action<IRobotDialogManager, IDisplayRobotProxy>(frame.addRobotButton), new object[] { irdm, rp });
            }
        }

        #endregion

        #region Immediate

        public bool isIconified()
        {
            if (closing)
                return true;

            return frame.isIconified();
        }


        public void dispose()
        {
        }

        public void showRobocodeFrame(bool b)
        {
        }

        #endregion
    }
}
