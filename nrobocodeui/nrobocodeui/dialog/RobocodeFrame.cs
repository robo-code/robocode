using java.io;
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
            Console.WriteLine("getBattleView Ha hah ah ah ah ah !!!!!");
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
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
            return "potemkin.battle";
        }

        public void setStatus(string str)
        {
            Console.WriteLine("setStatus Ha hah ah ah ah ah !!!!!");
        }

        public void dispose()
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
        }

        public void messageWarning(string str)
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
        }

        public void setIconified(bool b)
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
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
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
            return true;
        }

        public void messageError(string str)
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
        }
    }
}
