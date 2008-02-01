using System;
using nrobocodeui.utils;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.battleview
{
    /// <summary>
    /// Proxy
    /// </summary>
    public class BattleViewProxy : IBattleView
    {
        public BattleViewProxy(BattleView battleView)
        {
            this.battleView = battleView;
        }

        private BattleView battleView;


        public void setBattleField(BattleField value)
        {
            battleView.Invoke(new System.Action<BattleField>(battleView.setBattleField), new object[] { value });
        }

        public void setVisible(bool value)
        {
            battleView.Invoke(new System.Action<bool>(battleView.setVisible), new object[] { value });
        }

        public void setInitialized(bool value)
        {
            battleView.Invoke(new System.Action<bool>(battleView.setInitialized), new object[] { value });
        }

        public void setBattle(Battle value)
        {
            battleView.Invoke(new System.Action<Battle>(battleView.setBattle), new object[] { value });
        }

        public void repaint()
        {
            battleView.Invoke(new Action(battleView.repaint));
        }

        public void setDisplayOptions()
        {
            battleView.Invoke(new Action(battleView.setDisplayOptions));
        }

        public void update()
        {
            battleView.Invoke(new Action(battleView.update));
        }

        public bool isDisplayTPS()
        {
            return (bool)battleView.Invoke(new Delegate<bool>(battleView.isDisplayTPS));
        }

        public bool isDisplayFPS()
        {
            return (bool)battleView.Invoke(new Delegate<bool>(battleView.isDisplayFPS));
        }

        public int getWidth()
        {
            return (int)battleView.Invoke(new Delegate<int>(battleView.getWidth));
        }

        public int getHeight()
        {
            return (int)battleView.Invoke(new Delegate<int>(battleView.getHeight));
        }
    }
}
