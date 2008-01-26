using java.io;
using robocode.peer;
using nrobocodeui.battleview;
using robocode.ui;
using Console=System.Console;

namespace nrobocodeui.dialog
{
    class RobocodeFrameStub : IRobocodeFrame
    {
        private BattleViewStub battleView;
        public IBattleView getBattleView()
        {
            if (battleView==null)
            {
                battleView=new BattleViewStub();
            }
            return battleView;
        }

        public void setEnableBattleSaveAsMenuItem(bool b)
        {
            Console.WriteLine("setEnableBattleSaveAsMenuItem "+b);
        }

        public void setEnableBattleSaveMenuItem(bool b)
        {
            Console.WriteLine("setEnableBattleSaveMenuItem "+b);
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
            Console.WriteLine("setEnableStopButton");
        }

        public void setEnableRestartButton(bool b)
        {
            Console.WriteLine("setEnableRestartButton");
        }

        public void setEnableReplayButton(bool b)
        {
            Console.WriteLine("setEnableReplayButton");
        }

        public void clearRobotButtons()
        {
            Console.WriteLine("clearRobotButtons");
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            Console.WriteLine("addRobotButton");
        }

        public void validate()
        {
            Console.WriteLine("validate");
        }

        public bool isIconified()
        {
            return false;
        }

        public void messageError(string str)
        {
            Console.Error.WriteLine(str);
        }
    }
}
