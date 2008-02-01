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

using nrobocodeui.utils;
using robocode.peer;
using nrobocodeui.battleview;
using robocode.ui;

namespace nrobocodeui.dialog
{

    class RobocodeFrameProxy : IRobocodeFrame
    {
        public RobocodeFrameProxy(RobocodeFrame frame)
        {
            this.frame = frame;
        }

        private RobocodeFrame frame;

        private BattleViewProxy battleView;
        public IBattleView getBattleView()
        {
            if (battleView==null)
            {
                battleView=new BattleViewProxy();
            }
            return battleView;
        }

        public void setStatus(string value)
        {
            frame.Invoke(new Action<string>(frame.setStatus), new object[] { value });
        }

        public void setTitle(string value)
        {
            frame.Invoke(new Action<string>(frame.setTitle), new object[] { value });
        }

        public void messageError(string value)
        {
            frame.Invoke(new Action<string>(frame.messageWarning), new object[] { value });
        }

        public void messageWarning(string value)
        {
            frame.Invoke(new Action<string>(frame.messageWarning), new object[] { value });
        }

        public void setEnableBattleSaveAsMenuItem(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setEnableBattleSaveAsMenuItem), new object[] { value });
        }

        public void setEnableBattleSaveMenuItem(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setEnableBattleSaveMenuItem), new object[] { value });
        }

        public string saveBattleDialog(string file)
        {
            return frame.Invoke(new Delegate<string, string>(frame.saveBattleDialog), new object[] { file }) as string;
        }

        public void setIconified(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setIconified), new object[] { value });
        }

        public void setEnableStopButton(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setEnableStopButton), new object[] { value });
        }

        public void setEnableRestartButton(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setEnableRestartButton), new object[] { value });
        }

        public void setEnableReplayButton(bool value)
        {
            frame.Invoke(new Action<bool>(frame.setEnableReplayButton), new object[] { value });
        }

        public void validate()
        {
            frame.Invoke(new Action(frame.validate));
        }

        public void clearRobotButtons()
        {
            frame.Invoke(new Action(frame.clearRobotButtons));
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            frame.Invoke(new Action<IRobotDialogManager, RobotPeer>(frame.addRobotButton), new object[] { irdm, rp });
        }

        public bool isIconified()
        {
            return (bool)frame.Invoke(new Delegate<bool>(frame.isIconified));
        }


        public void dispose()
        {
        }

        public void showRobocodeFrame(bool b)
        {
        }
    }
}
