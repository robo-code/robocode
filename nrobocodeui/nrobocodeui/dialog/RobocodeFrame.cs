﻿using java.io;
using robocode.peer;
using nrobocodeui.battleview;
using robocode.ui;
using Console=System.Console;

namespace nrobocodeui.dialog
{
    class RobocodeFrame : IRobocodeFrame
    {
        private BattleView battleView;
        public IBattleView getBattleView()
        {
            if (battleView==null)
            {
                battleView=new BattleView();
            }
            return battleView;
        }

        public void setEnableBattleSaveAsMenuItem(bool b)
        {
            Console.WriteLine("setEnableBattleSaveAsMenuItem Ha hah ah ah ah ah !!!!!");
        }

        public void setEnableBattleSaveMenuItem(bool b)
        {
            Console.WriteLine("setEnableBattleSaveMenuItem Ha hah ah ah ah ah !!!!!");
        }

        public string saveBattleDialog(File f)
        {
            return "potemkin.battle";
        }

        public void setStatus(string str)
        {
            Console.WriteLine(str);
        }

        public void dispose()
        {
        }

        public void messageWarning(string str)
        {
            Console.WriteLine(str);
        }

        public void setIconified(bool b)
        {
        }

        public void setEnableStopButton(bool b)
        {
            Console.WriteLine("setEnableStopButton Ha hah ah ah ah ah !!!!!");
        }

        public void setEnableRestartButton(bool b)
        {
            Console.WriteLine("setEnableRestartButton Ha hah ah ah ah ah !!!!!");
        }

        public void setEnableReplayButton(bool b)
        {
            Console.WriteLine("setEnableReplayButton Ha hah ah ah ah ah !!!!!");
        }

        public void clearRobotButtons()
        {
            Console.WriteLine("clearRobotButtons Ha hah ah ah ah ah !!!!!");
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            Console.WriteLine("addRobotButton Ha hah ah ah ah ah !!!!!");
        }

        public void validate()
        {
            Console.WriteLine("validate Ha hah ah ah ah ah !!!!!");
        }

        public bool isIconified()
        {
            return true;
        }

        public void messageError(string str)
        {
            Console.Error.WriteLine(str);
        }
    }
}
