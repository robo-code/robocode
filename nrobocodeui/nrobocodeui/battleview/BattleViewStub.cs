using System;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.battleview
{
    class BattleViewStub : IBattleView
    {
        public void setBattleField(BattleField bf)
        {
            Console.WriteLine("setBattleField");
        }

        public void setVisible(bool b)
        {
            Console.WriteLine("setVisible "+b);
        }

        public void setInitialized(bool b)
        {
            Console.WriteLine("setInitialized "+b);
        }

        public void setBattle(Battle b)
        {
            Console.WriteLine("setBattle");
        }

        public void repaint()
        {
            Console.WriteLine("repaint");
        }

        public void setDisplayOptions()
        {
            Console.WriteLine("setDisplayOptions");
        }

        public void update()
        {
            Console.WriteLine("update");
        }

        public bool isDisplayTPS()
        {
            Console.WriteLine("isDisplayTPS");
            return false;
        }

        public bool isDisplayFPS()
        {
            Console.WriteLine("isDisplayFPS");
            return false;
        }

        public void setTitle(string str)
        {
            Console.WriteLine("setTitle "+str);
        }

        public int getWidth()
        {
            return 0;
        }

        public int getHeight()
        {
            return 0;
        }
    }
}
