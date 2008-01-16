using System;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.battleview
{
    class BattleView : IBattleView
    {
        public void setBattleField(BattleField bf)
        {
            Console.WriteLine("setBattleField Ha hah ah ah ah ah !!!!!");
        }

        public void setVisible(bool b)
        {
            Console.WriteLine("setVisible Ha hah ah ah ah ah !!!!!");
        }

        public void setInitialized(bool b)
        {
            Console.WriteLine("setInitialized Ha hah ah ah ah ah !!!!!");
        }

        public void setBattle(Battle b)
        {
            Console.WriteLine("setBattle Ha hah ah ah ah ah !!!!!");
        }

        public void repaint()
        {
            Console.WriteLine("repaint");
        }

        public void setDisplayOptions()
        {
            Console.WriteLine("setDisplayOptions Ha hah ah ah ah ah !!!!!");
        }

        public void update()
        {
            Console.WriteLine("update !!!!!");
        }

        public bool isDisplayTPS()
        {
            Console.WriteLine("isDisplayTPS Ha hah ah ah ah ah !!!!!");
            return false;
        }

        public bool isDisplayFPS()
        {
            Console.WriteLine("isDisplayFPS Ha hah ah ah ah ah !!!!!");
            return false;
        }

        public void setTitle(string str)
        {
            Console.WriteLine("setTitle Ha hah ah ah ah ah !!!!!");
        }

        public int getWidth()
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
            return 0;
        }

        public int getHeight()
        {
            Console.WriteLine("Ha hah ah ah ah ah !!!!!");
            return 0;
        }
    }
}
